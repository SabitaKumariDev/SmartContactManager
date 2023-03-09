console.log("this is script file")

const toggleSidebar = () =>{
	
	if($(".sidebar").is(":visible")){
		
		$(".sidebar").css("display","none");
		$(".content").css("margin-left", "0%");
	}
	else{
		
		$(".sidebar").css("display","block");
		$(".content").css("margin-left", "20%");
	}
};

const search = () => {
	//console.log("searching....");

	let query= $("#search-input").val();

	if(query=="")
	{
		$(".search-result").hide();
	}
	else
	{
		//search
		console.log(query);

		//sending request for server
		let url=`http://localhost:8080/search/${query}`;

		fetch(url).then((respose) => {
				return respose.json();
			})
			.then((data) => {

				let text= `<div class='list-group'>`;

				data.forEach((contact) => {
					text += `<a href='/user/${contact.cId}/contact' class='list-group-item list-group-action'> ${contact.name} </a>`
					
				});

				text += `</div>`;

				$(".search-result").html(text);
				$(".search-result").show();

			});
	}
};