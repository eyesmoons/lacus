/**
 * 湖智模块 - 数据集上传页交互逻辑
 */
(function() {
    'use strict';

    const LakeCommon = window.LakeCommon;
    let selectedFile = null;

    // 初始化
    document.addEventListener('DOMContentLoaded', function() {
        initSourceSwitch();
        initFileUpload();
        initSliders();
    });

    // 数据源类型切换
    function initSourceSwitch() {
        document.querySelectorAll('input[name="storageSource"]').forEach(function(radio) {
            radio.addEventListener('change', function() {
                document.querySelectorAll('.source-fields').forEach(function(el) {
                    el.classList.remove('active');
                });
                const target = document.getElementById('fields-' + this.value);
                if (target) {
                    target.classList.add('active');
                }
                // 清除之前的探测结果
                const probeResult = document.getElementById('probeResult');
                probeResult.className = 'mt-3 d-none';
                probeResult.innerHTML = '';
            });
        });
    }

    // 文件上传初始化
    function initFileUpload() {
        const fileInput = document.getElementById('fileInput');
        const uploadZone = document.getElementById('uploadZone');

        fileInput.addEventListener('change', function(e) {
            if (e.target.files.length > 0) {
                handleFile(e.target.files[0]);
            }
        });

        // 拖拽上传
        uploadZone.addEventListener('dragover', function(e) {
            e.preventDefault();
            this.classList.add('dragover');
        });
        uploadZone.addEventListener('dragleave', function() {
            this.classList.remove('dragover');
        });
        uploadZone.addEventListener('drop', function(e) {
            e.preventDefault();
            this.classList.remove('dragover');
            if (e.dataTransfer.files.length > 0) {
                const file = e.dataTransfer.files[0];
                if (file.name.endsWith('.zip')) {
                    handleFile(file);
                } else {
                    LakeCommon.showToast('请选择 .zip 格式的文件', 'warning');
                }
            }
        });

        // 表单提交
        document.getElementById('datasetForm').addEventListener('submit', function(e) {
            e.preventDefault();
            if (this.checkValidity()) {
                createDataset();
            } else {
                this.classList.add('was-validated');
            }
        });
    }

    // 处理选中文件
    function handleFile(file) {
        if (!file.name.endsWith('.zip')) {
            LakeCommon.showToast('仅支持 .zip 格式文件', 'warning');
            return;
        }
        const maxSize = 500 * 1024 * 1024; // 500MB
        if (file.size > maxSize) {
            LakeCommon.showToast('文件大小超过 500MB 限制', 'warning');
            return;
        }
        selectedFile = file;
        document.getElementById('fileName').textContent = file.name;
        document.getElementById('fileSize').textContent = LakeCommon.formatFileSize(file.size);
        document.getElementById('fileInfo').classList.remove('d-none');
    }

    // 清除文件
    window.clearFile = function() {
        selectedFile = null;
        document.getElementById('fileInput').value = '';
        document.getElementById('fileInfo').classList.add('d-none');
    };

    // 滑块初始化
    function initSliders() {
        const lrRange = document.getElementById('lrRange');
        const lrValue = document.getElementById('lrValue');
        if (lrRange) {
            lrRange.addEventListener('input', function() {
                const val = Math.pow(10, parseFloat(this.value));
                const display = val.toFixed(4).replace(/\.?0+$/, '');
                lrValue.textContent = display;
            });
        }
    }

    // 获取当前数据源配置
    function getSourceConfig() {
        const source = document.querySelector('input[name="storageSource"]:checked').value;
        const config = { storageSource: source };

        switch (source) {
            case 'LOCAL':
                if (selectedFile) {
                    config.uri = selectedFile.name;
                }
                break;
            case 'HDFS':
                config.uri = document.getElementById('hdfsUri').value;
                config.credentials = { user: document.getElementById('hdfsUser').value };
                break;
            case 'S3':
                config.uri = document.getElementById('s3Uri').value;
                config.credentials = {
                    accessKey: document.getElementById('s3AccessKey').value,
                    secretKey: document.getElementById('s3SecretKey').value,
                    region: document.getElementById('s3Region').value
                };
                break;
            case 'MINIO':
                config.uri = document.getElementById('minioUri').value;
                config.credentials = {
                    accessKey: document.getElementById('minioAccessKey').value,
                    secretKey: document.getElementById('minioSecretKey').value,
                    endpoint: document.getElementById('minioEndpoint').value
                };
                break;
            case 'HTTP':
                config.uri = document.getElementById('httpUrl').value;
                break;
        }
        return config;
    }

    // 测试连接
    window.testConnection = async function() {
        const config = getSourceConfig();
        const testBtn = document.getElementById('testBtn');
        const probeResult = document.getElementById('probeResult');

        if (!config.uri) {
            LakeCommon.showToast('请先填写数据源地址', 'warning');
            return;
        }

        LakeCommon.setLoading(testBtn, true, '测试中...');
        probeResult.className = 'mt-3 d-none';

        try {
            const result = await LakeCommon.request('/api/lake-intelligence/datasets/probe-source', {
                method: 'POST',
                body: JSON.stringify({ uri: config.uri })
            });

            probeResult.className = 'mt-3';
            if (result.accessible) {
                probeResult.innerHTML = `
                    <div class="alert alert-success mb-0">
                        <i class="bi bi-check-circle me-2"></i>
                        连接成功！发现 <strong>${result.file_count || 0}</strong> 个图片文件
                        ${result.total_size_human ? '，总大小 ' + result.total_size_human : ''}
                    </div>`;
            } else {
                probeResult.innerHTML = `
                    <div class="alert alert-danger mb-0">
                        <i class="bi bi-x-circle me-2"></i>
                        连接失败：${result.error || '未知错误'}
                    </div>`;
            }
        } catch (err) {
            probeResult.className = 'mt-3';
            probeResult.innerHTML = `
                <div class="alert alert-danger mb-0">
                    <i class="bi bi-x-circle me-2"></i>
                    测试失败：${err.message}
                </div>`;
        } finally {
            LakeCommon.setLoading(testBtn, false);
        }
    };

    // 创建数据集
    async function createDataset() {
        const config = getSourceConfig();
        const createBtn = document.getElementById('createBtn');

        if (!config.uri) {
            LakeCommon.showToast('请先选择文件或填写数据源地址', 'warning');
            return;
        }

        const payload = {
            datasetName: document.getElementById('datasetName').value,
            description: document.getElementById('description').value,
            storageSource: config.storageSource,
            sourceConfig: JSON.stringify(config),
            creatorId: 'current-user'
        };

        LakeCommon.setLoading(createBtn, true, '创建中...');

        try {
            const result = await LakeCommon.request('/api/lake-intelligence/datasets', {
                method: 'POST',
                body: JSON.stringify(payload)
            });
            LakeCommon.showToast('数据集创建成功！', 'success');
            setTimeout(function() {
                window.location.href = '/lake-intelligence/dataset/' + (result.datasetId || result) + '/preview';
            }, 1000);
        } catch (err) {
            LakeCommon.showToast('创建失败：' + err.message, 'error');
        } finally {
            LakeCommon.setLoading(createBtn, false);
        }
    }
})();
