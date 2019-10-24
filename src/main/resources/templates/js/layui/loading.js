//layui的loading效果****start
function ityzl_SHOW_LOAD_LAYER() {
    return layer.msg('数据加载中...', {
        icon: 16,
        shade: [0.5, '#f5f5f5'],
        scrollbar: false,
        area: '200px',
        time: 100000
    });
}

function ityzl_SHOW_LOAD_DOWNLOAD() {
    return layer.msg('正在下载,请稍后...', {
        icon: 16,
        shade: [0.5, '#f5f5f5'],
        scrollbar: false,
        area: '200px',
        time: 100000
    });
}

function ityzl_SHOW_LOAD_LAYER_WAVE() {
    return layer.msg('波形下载中', {
        icon: 16,
        shade: [0.5, '#f5f5f5'],
        scrollbar: false,
        time: 100000
    });
}


function ityzl_SHOW_LOAD_LAYER_PARENT() {
    return parent.layer.msg('数据加载中...', {
        icon: 16,
        shade: [0.5, '#f5f5f5'],
        scrollbar: false,
        time: 100000
    });
}

//layui的loading效果****end
function ityzl_CLOSE_LOAD_LAYER_PARENT(index) {
    parent.layer.close(index);
}

//layui的loading效果****end
function ityzl_CLOSE_LOAD_LAYER(index) {
    layer.close(index);
}

function SHOW_LOAD_LAYER(layer) {
    return layer.msg('数据加载中...', {
        icon: 16,
        shade: [0.5, '#f5f5f5'],
        scrollbar: false,
        time: 100000
    });
}

//layui的loading效果****end
function CLOSE_LOAD_LAYER(layer, index) {
    layer.close(index);
}

function time2String() {
    var date = new Date();
    var month = date.getMonth() + 1;
    var strDate = date.getDate();

    if (month >= 1 && month <= 9) {
        month = "0" + month;
    }
    if (strDate >= 0 && strDate <= 9) {
        strDate = "0" + strDate;
    }
    var strTime = date.getFullYear().toString() + month.toString() + strDate.toString()
        + date.getHours().toString() + date.getMinutes().toString()
        + date.getSeconds().toString();

    return strTime;
}

