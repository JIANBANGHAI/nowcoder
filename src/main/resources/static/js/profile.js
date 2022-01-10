$(function(){
	$(".follow-btn").click(follow);
});

function follow() {
	var btn = this;
	if($(btn).hasClass("btn-info")) {
		// 关注TA
		$.post(
			COUNT_PATH+"/follow",
			{"entityType":3,"entityId":$(btn).prev().val()},
			function (data) {
				data = $.parseJSON(data);
				if (data.code==200){
					window.location.reload();
				}
			}
		);

	} else {
		// 取消关注
		$.post(
			COUNT_PATH+"/follower",
			{"entityType":3,"entityId":$(btn).prev().val()},
			function (data) {
				data = $.parseJSON(data);
				if(data.code==200){
					window.location.reload();
				}else {
					alert(data.msg);
				}
			}
		);
	}
}