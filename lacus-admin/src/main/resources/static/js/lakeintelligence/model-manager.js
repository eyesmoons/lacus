/**
 * 湖智模块 - 模型管理页交互逻辑
 */
(function() {
    'use strict';

    const LakeCommon = window.LakeCommon;
    let currentPage = 1;
    let pageSize = 20;

    document.addEventListener('DOMContentLoaded', function() {
        loadModels();

        // 筛选事件
        document.getElementById('filterName').addEventListener('keyup', debounce(loadModels, 300));
        document.getElementById('filterArch').addEventListener('change', loadModels);
        document.getElementById('pageSize').addEventListener('change', function() {
            pageSize = parseInt(this.value);
            currentPage = 1;
            loadModels();
        });
    });

    // 加载模型列表
    async function loadModels() {
        const tbody = document.getElementById('modelTableBody');
        tbody.innerHTML = '<tr><td colspan="9" class="text-center py-5 text-muted">' +
            '<div class="spinner-border spinner-border-sm text-primary" role="status"></div>' +
            '<span class="ms-2">加载中...</span></td></tr>';

        const nameFilter = document.getElementById('filterName').value;
        const archFilter = document.getElementById('filterArch').value;

        try {
            const params = new URLSearchParams({
                page: currentPage,
                pageSize: pageSize
            });
            if (nameFilter) params.append('modelName', nameFilter);
            if (archFilter) params.append('modelArch', archFilter);

            const data = await LakeCommon.request('/api/lake-intelligence/models?' + params.toString());
            renderTable(data);
        } catch (err) {
            tbody.innerHTML = '<tr><td colspan="9" class="text-center py-5 text-danger">' +
                '<i class="bi bi-exclamation-triangle me-2"></i>加载失败：' + err.message + '</td></tr>';
        }
    }

    // 渲染表格
    function renderTable(data) {
        const tbody = document.getElementById('modelTableBody');
        const list = data.rows || data.list || [];
        const total = data.total || list.length;

        document.getElementById('tableInfo').textContent = '共 ' + total + ' 条记录';

        if (list.length === 0) {
            tbody.innerHTML = '<tr><td colspan="9" class="text-center py-5 text-muted">' +
                '<i class="bi bi-inbox fs-2 d-block mb-2"></i>暂无模型数据</td></tr>';
            renderPagination(0);
            return;
        }

        tbody.innerHTML = '';
        list.forEach(function(m, idx) {
            const row = document.createElement('tr');
            row.innerHTML = '<td>' + ((currentPage - 1) * pageSize + idx + 1) + '</td>' +
                '<td class="fw-medium">' + (m.modelName || '-') + '</td>' +
                '<td><code>' + (m.modelArch || '-') + '</code></td>' +
                '<td>' + (m.embeddingDim || '-') + '</td>' +
                '<td>' + (m.trainingEpochs || '-') + '</td>' +
                '<td>' + (m.finalLoss !== null && m.finalLoss !== undefined ? m.finalLoss.toFixed(4) : '-') + '</td>' +
                '<td>' + LakeCommon.formatFileSize(m.modelSizeBytes) + '</td>' +
                '<td>' + LakeCommon.formatDate(m.createTime) + '</td>' +
                '<td>' +
                    '<div class="btn-group btn-group-sm">' +
                        '<a href="/api/lake-intelligence/models/' + m.modelId + '/download" ' +
                           'class="btn btn-outline-primary" title="下载">' +
                            '<i class="bi bi-download"></i></a>' +
                        '<button type="button" class="btn btn-outline-danger" ' +
                           'onclick="deleteModel(' + m.modelId + ')" title="删除">' +
                            '<i class="bi bi-trash"></i></button>' +
                    '</div>' +
                '</td>';
            tbody.appendChild(row);
        });

        renderPagination(Math.ceil(total / pageSize));
    }

    // 渲染分页
    function renderPagination(totalPages) {
        const pag = document.getElementById('pagination');
        if (totalPages <= 1) {
            pag.innerHTML = '';
            return;
        }

        let html = '';
        html += '<li class="page-item ' + (currentPage <= 1 ? 'disabled' : '') + '">' +
            '<a class="page-link" href="javascript:goPage(' + (currentPage - 1) + ')">上一页</a></li>';

        for (let i = 1; i <= totalPages; i++) {
            if (i === 1 || i === totalPages || (i >= currentPage - 2 && i <= currentPage + 2)) {
                html += '<li class="page-item ' + (i === currentPage ? 'active' : '') + '">' +
                    '<a class="page-link" href="javascript:goPage(' + i + ')">' + i + '</a></li>';
            } else if (i === currentPage - 3 || i === currentPage + 3) {
                html += '<li class="page-item disabled"><span class="page-link">...</span></li>';
            }
        }

        html += '<li class="page-item ' + (currentPage >= totalPages ? 'disabled' : '') + '">' +
            '<a class="page-link" href="javascript:goPage(' + (currentPage + 1) + ')">下一页</a></li>';

        pag.innerHTML = html;
    }

    // 翻页
    window.goPage = function(page) {
        if (page < 1) return;
        currentPage = page;
        loadModels();
    };

    // 删除模型
    window.deleteModel = async function(modelId) {
        if (!confirm('确认删除此模型？此操作不可恢复。')) return;

        try {
            await LakeCommon.request('/api/lake-intelligence/models/' + modelId, {
                method: 'DELETE'
            });
            LakeCommon.showToast('模型已删除', 'success');
            loadModels();
        } catch (err) {
            LakeCommon.showToast('删除失败：' + err.message, 'error');
        }
    };

    // 防抖函数
    function debounce(fn, delay) {
        let timer;
        return function() {
            clearTimeout(timer);
            timer = setTimeout(fn, delay);
        };
    }
})();
