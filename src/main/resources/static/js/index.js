$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");
	//获取页面元素
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();

	//发送异步请求
	$.post(
		COUNT_PATH+ "/discuss/addDiscuss",
		{"title":title,"content":content},
		function (data) {
			data = $.parseJSON(data);
			$("#hintBody").text(data.msg);
			$("#hintModal").modal("show");

			setTimeout(function(){
				$("#hintModal").modal("hide");
				if (data.code==200){
					window.location.reload();
				}
			}, 2000);
		}
	)

}