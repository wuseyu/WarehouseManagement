/**
 * 用户管理模块
 */
const UserManagement = {
    // 初始化
    init: function() {
        this.loadUsers();
        this.loadRoles();
        this.bindEvents();
    },
    
    // 绑定事件
    bindEvents: function() {
        // 添加用户按钮
        $('#addUserBtn').on('click', function() {
            UserManagement.resetForm();
            $('#userModalTitle').text('添加用户');
            $('#passwordHelpText').text('新建用户必须设置密码');
            $('#password').attr('required', 'required');
            $('#userModal').modal('show');
        });
        
        // 保存用户按钮
        $('#saveUserBtn').on('click', function() {
            UserManagement.saveUser();
        });
    },
    
    // 重置表单
    resetForm: function() {
        $('#userForm')[0].reset();
        $('#userId').val('');
        $('.is-invalid').removeClass('is-invalid');
        $('.invalid-feedback').remove();
    },
    
    // 加载所有用户
    loadUsers: function() {
        $.ajax({
            url: '/api/users',
            type: 'GET',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('token')
            },
            success: function(users) {
                let html = '';
                users.forEach(function(user) {
                    html += `
                        <tr>
                            <td>${user.id}</td>
                            <td>${user.username}</td>
                            <td>${user.email || '-'}</td>
                            <td>${user.phone || '-'}</td>
                            <td>${UserManagement.formatRoles(user.roles)}</td>
                            <td>${new Date(user.createdAt).toLocaleString()}</td>
                            <td>
                                <button class="btn btn-sm btn-primary edit-user" data-id="${user.id}">
                                    <i class="bi bi-pencil"></i>
                                </button>
                                <button class="btn btn-sm btn-danger delete-user" data-id="${user.id}">
                                    <i class="bi bi-trash"></i>
                                </button>
                            </td>
                        </tr>
                    `;
                });
                
                $('#userTableBody').html(html);
                
                // 绑定编辑按钮事件
                $('.edit-user').on('click', function() {
                    const userId = $(this).data('id');
                    UserManagement.editUser(userId);
                });
                
                // 绑定删除按钮事件
                $('.delete-user').on('click', function() {
                    const userId = $(this).data('id');
                    UserManagement.deleteUser(userId);
                });
            },
            error: function(xhr) {
                console.error('加载用户失败:', xhr);
                alert('加载用户失败: ' + xhr.responseText);
            }
        });
    },
    
    // 加载所有角色
    loadRoles: function() {
        $.ajax({
            url: '/api/users/roles',
            type: 'GET',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('token')
            },
            success: function(roles) {
                let html = '';
                roles.forEach(function(role) {
                    html += `
                        <div class="form-check">
                            <input class="form-check-input role-checkbox" type="checkbox" value="${role.id}" id="role${role.id}">
                            <label class="form-check-label" for="role${role.id}">
                                ${role.name} ${role.description ? '(' + role.description + ')' : ''}
                            </label>
                        </div>
                    `;
                });
                $('#roleCheckboxes').html(html);
            },
            error: function(xhr) {
                console.error('加载角色失败:', xhr);
                alert('加载角色失败: ' + xhr.responseText);
            }
        });
    },
    
    // 编辑用户
    editUser: function(userId) {
        $.ajax({
            url: `/api/users/${userId}`,
            type: 'GET',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('token')
            },
            success: function(user) {
                UserManagement.resetForm();
                
                $('#userModalTitle').text('编辑用户');
                $('#userId').val(user.id);
                $('#username').val(user.username);
                $('#email').val(user.email || '');
                $('#phone').val(user.phone || '');
                
                // 密码字段不显示，并提示
                $('#password').removeAttr('required');
                $('#passwordHelpText').text('留空表示不修改密码');
                
                // 选中用户角色
                setTimeout(function() {
                    if (user.roles && user.roles.length > 0) {
                        user.roles.forEach(function(role) {
                            $(`#role${role.id}`).prop('checked', true);
                        });
                    }
                }, 100); // 等待角色复选框渲染完成
                
                $('#userModal').modal('show');
            },
            error: function(xhr) {
                console.error('加载用户详情失败:', xhr);
                alert('加载用户详情失败: ' + xhr.responseText);
            }
        });
    },
    
    // 保存用户
    saveUser: function() {
        const userId = $('#userId').val();
        
        // 表单验证
        if (!this.validateForm()) {
            return;
        }
        
        // 构建用户对象
        const user = {
            username: $('#username').val(),
            email: $('#email').val() || null,
            phone: $('#phone').val() || null
        };
        
        // 如果是新用户或者提供了密码，则包含密码字段
        const password = $('#password').val();
        if (password) {
            user.password = password;
        } else if (!userId) {
            // 新用户必须有密码
            this.showError($('#password'), '密码不能为空');
            return;
        }
        
        // 获取选中的角色ID
        const roleIds = [];
        $('.role-checkbox:checked').each(function() {
            roleIds.push($(this).val());
        });
        
        // 构建请求参数
        const url = userId ? `/api/users/${userId}` : '/api/users';
        const method = userId ? 'PUT' : 'POST';
        
        // 发送请求
        $.ajax({
            url: url,
            type: method,
            contentType: 'application/json',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('token')
            },
            data: JSON.stringify(user),
            success: function(response) {
                if (roleIds.length > 0) {
                    // 保存成功后设置角色
                    UserManagement.saveUserRoles(response.id || userId, roleIds);
                } else {
                    UserManagement.onSaveSuccess();
                }
            },
            error: function(xhr) {
                console.error('保存用户失败:', xhr);
                alert('保存用户失败: ' + xhr.responseJSON || xhr.responseText);
            }
        });
    },
    
    // 保存用户角色
    saveUserRoles: function(userId, roleIds) {
        $.ajax({
            url: `/api/users/${userId}/roles`,
            type: 'PUT',
            contentType: 'application/json',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('token')
            },
            data: JSON.stringify(roleIds),
            success: function() {
                UserManagement.onSaveSuccess();
            },
            error: function(xhr) {
                console.error('保存用户角色失败:', xhr);
                alert('保存用户角色失败: ' + (xhr.responseJSON || xhr.responseText));
                // 重新加载用户列表，显示部分更新的结果
                UserManagement.loadUsers();
            }
        });
    },
    
    // 保存成功后的处理
    onSaveSuccess: function() {
        $('#userModal').modal('hide');
        this.loadUsers();
        this.resetForm();
    },
    
    // 删除用户
    deleteUser: function(userId) {
        if (!confirm('确定要删除该用户吗？此操作不可恢复。')) {
            return;
        }
        
        $.ajax({
            url: `/api/users/${userId}`,
            type: 'DELETE',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('token')
            },
            success: function() {
                UserManagement.loadUsers();
            },
            error: function(xhr) {
                console.error('删除用户失败:', xhr);
                alert('删除用户失败: ' + xhr.responseText);
            }
        });
    },
    
    // 表单验证
    validateForm: function() {
        let isValid = true;
        
        // 移除之前的错误
        $('.is-invalid').removeClass('is-invalid');
        $('.invalid-feedback').remove();
        
        // 验证用户名
        const username = $('#username').val();
        if (!username) {
            this.showError($('#username'), '用户名不能为空');
            isValid = false;
        }
        
        // 验证邮箱格式
        const email = $('#email').val();
        if (email && !this.isValidEmail(email)) {
            this.showError($('#email'), '邮箱格式不正确');
            isValid = false;
        }
        
        return isValid;
    },
    
    // 显示错误
    showError: function(element, message) {
        element.addClass('is-invalid');
        element.after(`<div class="invalid-feedback">${message}</div>`);
    },
    
    // 验证邮箱格式
    isValidEmail: function(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    },
    
    // 格式化角色显示
    formatRoles: function(roles) {
        if (!roles || roles.length === 0) {
            return '无角色';
        }
        
        return roles.map(role => role.name).join(', ');
    }
};

// 页面加载完成后初始化
$(document).ready(function() {
    UserManagement.init();
}); 