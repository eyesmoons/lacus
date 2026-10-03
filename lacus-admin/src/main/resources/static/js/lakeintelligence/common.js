/**
 * 湖智模块 - 公共工具函数
 */
(function(window) {
    'use strict';

    // 显示 Toast 通知
    function showToast(message, type = 'info') {
        const container = document.getElementById('toastContainer');
        if (!container) {
            alert(message);
            return;
        }
        const icons = {
            success: 'bi-check-circle-fill text-success',
            error: 'bi-exclamation-circle-fill text-danger',
            warning: 'bi-exclamation-triangle-fill text-warning',
            info: 'bi-info-circle-fill text-info'
        };
        const toastId = 'toast-' + Date.now();
        const html = `
            <div id="${toastId}" class="toast align-items-center border-0 mb-2" role="alert" aria-live="assertive" aria-atomic="true">
                <div class="d-flex">
                    <div class="toast-body d-flex align-items-center">
                        <i class="bi ${icons[type] || icons.info} me-2"></i>
                        <span>${message}</span>
                    </div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
                </div>
            </div>
        `;
        container.insertAdjacentHTML('beforeend', html);
        const toastEl = document.getElementById(toastId);
        const toast = new bootstrap.Toast(toastEl, { delay: 4000 });
        toast.show();
        toastEl.addEventListener('hidden.bs.toast', () => toastEl.remove());
    }

    // 通用 fetch 封装
    function request(url, options = {}) {
        const defaultOptions = {
            headers: { 'Content-Type': 'application/json' }
        };
        return fetch(url, Object.assign(defaultOptions, options))
            .then(resp => {
                if (!resp.ok) {
                    throw new Error('HTTP ' + resp.status);
                }
                return resp.json();
            })
            .then(result => {
                if (result.code !== undefined && result.code !== 0 && result.code !== 200) {
                    throw new Error(result.msg || '请求失败');
                }
                return result.data !== undefined ? result.data : result;
            });
    }

    // 格式化文件大小
    function formatFileSize(bytes) {
        if (!bytes) return '0 B';
        const units = ['B', 'KB', 'MB', 'GB'];
        let i = 0;
        while (bytes >= 1024 && i < units.length - 1) {
            bytes /= 1024;
            i++;
        }
        return bytes.toFixed(bytes < 10 && i > 0 ? 1 : 0) + ' ' + units[i];
    }

    // 格式化日期时间
    function formatDate(dateStr) {
        if (!dateStr) return '-';
        const d = new Date(dateStr);
        if (isNaN(d.getTime())) return dateStr;
        return d.toLocaleString('zh-CN', {
            year: 'numeric', month: '2-digit', day: '2-digit',
            hour: '2-digit', minute: '2-digit'
        });
    }

    // 显示/隐藏加载状态
    function setLoading(btn, loading, text) {
        if (loading) {
            btn.dataset.originalText = btn.innerHTML;
            btn.innerHTML = `<span class="spinner-border spinner-border-sm me-2" role="status"></span>${text || '处理中...'}`;
            btn.disabled = true;
        } else {
            btn.innerHTML = btn.dataset.originalText || text || btn.innerHTML;
            btn.disabled = false;
        }
    }

    window.LakeCommon = {
        showToast: showToast,
        request: request,
        formatFileSize: formatFileSize,
        formatDate: formatDate,
        setLoading: setLoading
    };
})(window);
