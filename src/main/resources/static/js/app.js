// 添加车辆管理的路由配置
const vehiclesRoute = {
    path: '/vehicles',
    component: {
        template: `
            <div class="container">
                <h2 class="mb-4">车辆管理</h2>
                <div class="mb-3">
                    <button class="btn btn-primary" @click="showAddVehicleModal">添加车辆</button>
                </div>
                
                <div class="card mb-4">
                    <div class="card-header">
                        <div class="row">
                            <div class="col-md-6">
                                <h5>车辆列表</h5>
                            </div>
                            <div class="col-md-6 d-flex justify-content-end">
                                <select class="form-select me-2" style="width: auto;" v-model="statusFilter">
                                    <option value="">全部状态</option>
                                    <option value="AVAILABLE">可用</option>
                                    <option value="IN_USE">使用中</option>
                                    <option value="MAINTENANCE">维护中</option>
                                    <option value="PENDING">待处理</option>
                                </select>
                                <button class="btn btn-primary me-2" @click="applyFilter">
                                    <i class="fas fa-search"></i> 筛选
                                </button>
                                <button class="btn btn-secondary" @click="resetFilter">
                                    <i class="fas fa-sync"></i> 重置
                                </button>
                            </div>
                        </div>
                    </div>
                    <div class="card-body">
                        <div v-if="loading" class="text-center">
                            <div class="spinner-border" role="status">
                                <span class="visually-hidden">加载中...</span>
                            </div>
                        </div>
                        <div v-else-if="vehicles.length === 0" class="alert alert-info">
                            没有找到车辆数据
                        </div>
                        <div v-else class="table-responsive">
                            <table class="table table-striped table-hover">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>车牌号</th>
                                        <th>司机</th>
                                        <th>容量(m³)</th>
                                        <th>状态</th>
                                        <th>当前位置</th>
                                        <th>保险详情</th>
                                        <th>操作</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr v-for="vehicle in vehicles" :key="vehicle.id">
                                        <td>{{ vehicle.id }}</td>
                                        <td>{{ vehicle.plateNumber }}</td>
                                        <td>{{ vehicle.driverName }}</td>
                                        <td>{{ vehicle.capacity }}</td>
                                        <td>
                                            <span :class="getStatusBadgeClass(vehicle.status)">
                                                {{ getStatusText(vehicle.status) }}
                                            </span>
                                        </td>
                                        <td>{{ vehicle.currentLocation }}</td>
                                        <td>{{ vehicle.insuranceDetails }}</td>
                                        <td>
                                            <button class="btn btn-sm btn-info me-1" @click="editVehicle(vehicle)">
                                                编辑
                                            </button>
                                            <button class="btn btn-sm btn-danger" @click="deleteVehicle(vehicle)">
                                                删除
                                            </button>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
                
                <!-- 添加/编辑车辆模态框 -->
                <div class="modal fade" id="vehicleModal" tabindex="-1" aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title">{{ isEditing ? '编辑车辆' : '添加车辆' }}</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <div class="mb-3">
                                    <label for="plateNumber" class="form-label">车牌号</label>
                                    <input type="text" class="form-control" id="plateNumber" v-model="currentVehicle.plateNumber" required>
                                </div>
                                <div class="mb-3">
                                    <label for="driverName" class="form-label">司机姓名</label>
                                    <input type="text" class="form-control" id="driverName" v-model="currentVehicle.driverName">
                                </div>
                                <div class="mb-3">
                                    <label for="capacity" class="form-label">车辆容量(m³)</label>
                                    <input type="number" class="form-control" id="capacity" v-model="currentVehicle.capacity" required>
                                </div>
                                <div class="mb-3">
                                    <label for="status" class="form-label">状态</label>
                                    <select class="form-select" id="status" v-model="currentVehicle.status">
                                        <option value="AVAILABLE">可用</option>
                                        <option value="IN_USE">使用中</option>
                                        <option value="MAINTENANCE">维护中</option>
                                        <option value="PENDING">待处理</option>
                                    </select>
                                </div>
                                <div class="mb-3">
                                    <label for="currentLocation" class="form-label">当前位置</label>
                                    <input type="text" class="form-control" id="currentLocation" v-model="currentVehicle.currentLocation">
                                </div>
                                <div class="mb-3">
                                    <label for="insuranceDetails" class="form-label">保险详情</label>
                                    <textarea class="form-control" id="insuranceDetails" v-model="currentVehicle.insuranceDetails" rows="2"></textarea>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                <button type="button" class="btn btn-primary" @click="saveVehicle">保存</button>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- 确认删除模态框 -->
                <div class="modal fade" id="deleteConfirmModal" tabindex="-1" aria-hidden="true">
                    <div class="modal-dialog">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title">确认删除</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                确定要删除车牌号为 <strong>{{ currentVehicle.plateNumber }}</strong> 的车辆吗？此操作不可撤销。
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                <button type="button" class="btn btn-danger" @click="confirmDelete">删除</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `,
        data() {
            return {
                vehicles: [],
                currentVehicle: {},
                isEditing: false,
                loading: true,
                statusFilter: '',
                vehicleModal: null,
                deleteConfirmModal: null,
                allVehicles: []
            }
        },
        mounted() {
            this.loadVehicles();
            this.vehicleModal = new bootstrap.Modal(document.getElementById('vehicleModal'));
            this.deleteConfirmModal = new bootstrap.Modal(document.getElementById('deleteConfirmModal'));
        },
        methods: {
            loadVehicles() {
                this.loading = true;
                
                fetch('/api/vehicles', {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem('token')}`,
                        'Accept': 'application/json'
                    }
                })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`无法获取车辆数据: ${response.status} ${response.statusText}`);
                    }
                    return response.json();
                })
                .then(data => {
                    this.allVehicles = data; // 保存所有车辆数据
                    this.applyFilterToData(); // 应用筛选
                    this.loading = false;
                })
                .catch(error => {
                    console.error('获取车辆数据失败:', error);
                    alert('获取车辆数据失败: ' + error.message);
                    this.loading = false;
                });
            },
            
            // 应用筛选条件到已加载的数据
            applyFilterToData() {
                if (!this.statusFilter) {
                    this.vehicles = this.allVehicles;
                } else {
                    this.vehicles = this.allVehicles.filter(vehicle => 
                        vehicle.status === this.statusFilter
                    );
                }
                console.log(`筛选后显示 ${this.vehicles.length} 条记录，筛选条件:`, this.statusFilter || '全部');
            },
            
            // 用户点击筛选按钮
            applyFilter() {
                console.log('应用筛选条件:', this.statusFilter || '全部');
                this.applyFilterToData();
            },
            
            // 重置筛选条件
            resetFilter() {
                this.statusFilter = '';
                this.vehicles = this.allVehicles;
                console.log('重置筛选条件，显示全部记录');
            },
            showAddVehicleModal() {
                this.currentVehicle = {
                    plateNumber: '',
                    driverName: '',
                    capacity: 10,
                    status: 'AVAILABLE',
                    currentLocation: '',
                    insuranceDetails: ''
                };
                this.isEditing = false;
                this.vehicleModal.show();
            },
            editVehicle(vehicle) {
                this.currentVehicle = { ...vehicle };
                this.isEditing = true;
                this.vehicleModal.show();
            },
            saveVehicle() {
                const url = this.isEditing ? `/api/vehicles/${this.currentVehicle.id}` : '/api/vehicles';
                const method = this.isEditing ? 'PUT' : 'POST';
                
                fetch(url, {
                    method: method,
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${localStorage.getItem('token')}`
                    },
                    body: JSON.stringify(this.currentVehicle)
                })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('保存车辆失败');
                    }
                    return response.json();
                })
                .then(() => {
                    this.vehicleModal.hide();
                    this.loadVehicles();
                    alert(this.isEditing ? '车辆更新成功' : '车辆添加成功');
                })
                .catch(error => {
                    console.error('保存车辆失败:', error);
                    alert('保存车辆失败: ' + error.message);
                });
            },
            deleteVehicle(vehicle) {
                this.currentVehicle = vehicle;
                this.deleteConfirmModal.show();
            },
            confirmDelete() {
                fetch(`/api/vehicles/${this.currentVehicle.id}`, {
                    method: 'DELETE',
                    headers: {
                        'Authorization': `Bearer ${localStorage.getItem('token')}`
                    }
                })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('删除车辆失败');
                    }
                    this.deleteConfirmModal.hide();
                    this.loadVehicles();
                    alert('车辆删除成功');
                })
                .catch(error => {
                    console.error('删除车辆失败:', error);
                    alert('删除车辆失败: ' + error.message);
                });
            },
            getStatusText(status) {
                switch(status) {
                    case 'AVAILABLE': return '可用';
                    case 'IN_USE': return '使用中';
                    case 'MAINTENANCE': return '维护中';
                    case 'PENDING': return '待处理';
                    default: return status;
                }
            },
            getStatusBadgeClass(status) {
                switch(status) {
                    case 'AVAILABLE': return 'badge bg-success';
                    case 'IN_USE': return 'badge bg-primary';
                    case 'MAINTENANCE': return 'badge bg-warning';
                    case 'PENDING': return 'badge bg-info';
                    default: return 'badge bg-secondary';
                }
            }
        }
    }
};

// 将车辆路由添加到路由数组
routes.push(vehiclesRoute);

// 在侧边栏菜单中添加车辆管理选项
const sidebarMenuItems = [
    // ... existing menu items ...
    {
        id: 'vehicles',
        icon: 'fa-truck',
        text: '车辆管理',
        link: '#/vehicles'
    },
    // ... other menu items ...
]; 