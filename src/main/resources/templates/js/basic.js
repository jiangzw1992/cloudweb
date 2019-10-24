//页面元素响应式高度处理
$(function () {
    var height = $(window).height();
    $(".leftBorder").css("height", height - 35);
    $(".rightBorder").css("height", height - 35);
    $(".inSystem").css("top", $(".rightBorder").height() * 0.035);
    var titleHeight = $(".title").height();
    $(".titleLine").css("top", titleHeight + 20);
    $(".dataContent").css("top", titleHeight + 40);
    $(".dataContent").css("height", height - titleHeight - 80);
    var dataContent = $(".dataContent").height();
    $(".mapDiv").css("height", dataContent - 30);
    $(".dataDiv").css("height", (dataContent - 60) / 3);
    var divWidth = $(".dataDiv").width();
    var divHeight = $(".dataDiv").innerHeight();
    $(".subTitle").css("left", (divWidth - 60) / 2);
    $(".titleOne").css("top", 8);
    $(".titleTwo").css("top", divHeight + 28);
    $(".titleThree").css("top", (divHeight + 20) * 2 + 14);
    //媒体查询高度
    if ($(window).height() > 900) {
        $(".subOne,.subTwo,.subThree,.subFour,.subFive").css("margin-top", (divHeight - 150) / 2);
    } else {
        $(".subOne,.subTwo,.subThree,.subFour,.subFive").css("margin-top", (divHeight - 125) / 2);
    }
    $(".contribution,.contributionDiv").css("height", ($(".dataDiv").height() - 35) / 2);
    var conHeight = $(".contributionDiv").height();
    //媒体查询宽度
    if ($(window).width() < 1400) {
        $(".contributionDiv").css("padding-top", (conHeight - 37) / 2);
        $(".contributionDiv").css("padding-bottom", (conHeight - 37) / 2);
    } else {
        $(".contributionDiv").css("padding-top", (conHeight - 45) / 2);
        $(".contributionDiv").css("padding-bottom", (conHeight - 45) / 2);
    }
    //时钟功能
    var seconds = new Date().getSeconds();
    var minutes = new Date().getMinutes();
    var hours = new Date().getHours();
    $("#hours").html((hours < 10 ? "0" : "") + hours);
    $("#min").html((minutes < 10 ? "0" : "") + minutes);
    $("#sec").html((seconds < 10 ? "0" : "") + seconds);
    var monthNames = ["1.", "2.", "3.", "4.", "5.", "6.", "7.", "8.", "9.", "10.", "11.", "12."];
    var newDate = new Date();
    newDate.setDate(newDate.getDate());
    $('#Date').html(newDate.getFullYear() + "." + monthNames[newDate.getMonth()] + newDate.getDate());
    setInterval(function () {
        var newDate = new Date();
        newDate.setDate(newDate.getDate());
        $('#Date').html(newDate.getFullYear() + "." + monthNames[newDate.getMonth()] + newDate.getDate());
    }, 1000);
    setInterval(function () {
        var seconds = new Date().getSeconds();
        $("#sec").html((seconds < 10 ? "0" : "") + seconds);
    }, 1000);
    setInterval(function () {
        var minutes = new Date().getMinutes();
        $("#min").html((minutes < 10 ? "0" : "") + minutes);
    }, 1000);
    setInterval(function () {
        var hours = new Date().getHours();
        $("#hours").html((hours < 10 ? "0" : "") + hours);
    }, 1000);
    var divHeight = $(".dataDiv").height();
    $("#warn,#device,#online,#dayData,#monthData,#yearData,#inner,#outer,#output,#input").css("height", divHeight);
})
