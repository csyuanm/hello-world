var baseUrl = getRootUrl();
$(function(){
	$(".js-example-basic-single").select2();
	var h = $("body").height() - 300;
	$(".goBack").on("click",function(){
		history.go(-1);
	});
	laydate.render({
		elem: '#startDate', //指定元素
	});	
	laydate.render({
		elem: '#endDate', //指定元素
	});	
	var location = trim($(".warehouse").text());
	createGuide(location);
	function createGuide(location){
		$.ajax({
			url:baseUrl + "/reports/terminal/empListQuery",
			type:"POST",
			cache:false,
			data:{location_name:location},
			success:function(res){
				var data = JSON.parse(res);
				var dataArr = data.data;
				var option = "<option></option>";
				for(var i=0;i<dataArr.length;i++){
					option += "<option value='"+dataArr[i].entityid+"' data-name='"+dataArr[i].salesman_name+"'>"+dataArr[i].salesman_name+"</option>";
				}
				$("#guides").html(option);
				$(".js-example-basic-single").select2();
			}
		});
	}
	$("#terminal").on("change",function(){
		var location_name = $(this).val();
		createGuide(location_name);
	});
	$("#terminal").val(location).trigger("change");
	var columns = [
	   	{
	   		"data": "startNo"  //行号
	   	},
		{
			"data": "job_number" //工号
		},
		{
			"data": "store" //门店
		},
		{
			"data": "target" //指标
		},
		{
			"data": "sales" //销售额
		},
		{
			"data":"sales_difference" //差额
		},
		{
			"data":"standard_rate" //比率
		},
		{
			"data":"create_time",//日期
		}
	];
	var columnDefs = [
//    	{
//    		targets:1,
//    		visible:true
//    	}
    ];
	var drawCallback = function(settings){
		 var api = this.api();
            var rows = api.rows({
                page: 'current'
            }).nodes();
           var tableData = rows.data();
           var target = 0,amount = 0,sales_difference = 0,standard_rate = 0;
           var len = tableData.length;
           var flag = false;
           var _index = 0;
           tableData.each(function(data,index){	        	  
        	   target += parseFloat(data.target);
        	   amount += parseFloat(data.sales);
        	   sales_difference += parseFloat(data.sales_difference);
        	   standard_rate += parseFloat(data.standard_rate || 0);
        	   var job_number_next = index == len -1 ? null : tableData[index+1].job_number;
        	   if(job_number_next !== tableData[index].job_number){
        		   var target_1 =0,amount_1 = 0,sales_difference_1 = 0,standard_rate_1 = 0;
        		   for(var i=_index;i<index+1;i++){
        			   target_1 += parseFloat(tableData[i].target);
        			   amount_1 += parseFloat(tableData[i].sales);
        			   sales_difference_1 += parseFloat(tableData[i].sales_difference);
        			   standard_rate_1 += parseFloat(tableData[i].standard_rate || 0);
        		   }
        		   target_1 = target_1.toFixed(2);
        		   amount_1 = amount_1.toFixed(2);
        		   sales_difference_1 = sales_difference_1.toFixed(2);
        		   standard_rate_1 = target_1 == "0.00" ? "0.00%" : (100*(amount_1 / target_1)).toFixed(2) + "%";
        		   var tr = '<tr class="group"><td colspan="3">'+ tableData[index].job_number + '</td>';
        		   tr += '<td>'+target_1+'</td><td>'+amount_1+'</td><td>'+sales_difference_1+'</td><td>'+standard_rate_1+'</td><td></td></tr>';
        		   $(rows).eq(index).after(tr);
        		   _index = index+1;
        	   }
        	   
           });
           if(flag){
        	   target = target.toFixed(2);
        	   amount = amount.toFixed(2);
        	   sales_difference = sales_difference.toFixed(2);
        	   standard_rate = target == "0.00" ? "0.00%" : (100*(amount / target)).toFixed(2) + "%";
        	   $(rows).eq(len-1).after('<tr class="group"><td colspan="3">'
        			   + tableData[len-1].job_number + '</td><td>'+target+'<td>'
        			   +amount+'</td><td>'+sales_difference+'</td><td>'+standard_rate+'</td><td></td>'
        			   +'</tr>');
        	   }
	}
	var params = {
			startDate : $("#startDate").val() || "",
			endDate : $("#endDate").val() || "",
			terminal : $("#terminal").find("option:selected").text()|| "",
			guides :$("#guides").find("option:selected").text()|| ""
	}; 
	var settings = {
			id:"#example",
			url:"/reports/guide/queryList",
			height:h,
			param:params,
			columns:columns,
			columnDefs:columnDefs,
			drawCallback:drawCallback
	}
	var table = invokeTable(settings);
	$(".searchBtn").on("click",function(){
		var params = {
				startDate : $("#startDate").val() || "",
				endDate : $("#endDate").val() || "",
				terminal : $("#terminal").find("option:selected").text()|| "",
				guides :$("#guides").find("option:selected").text()|| ""
		}; 
		var settings = {
				id:"#example",
				url:"/reports/guide/queryList",
				height:h,
				param:params,
				columns:columns,
				columnDefs:columnDefs,
				drawCallback:drawCallback
		}
		invokeTable(settings);
	});
	$(".clearBtn").on("click",function(){
		$("#startDate").val("");
		$("#endDate").val("");
		$("#terminal").val(location).trigger("change");
		$("#guides").val("");
		$("#select2-guides-container").empty();
		invokeTable(settings);
	});
	$("#exportBtn").on("click",function(){
		var url = baseUrl + "/reports/guide/load";
		exportExcel(url,"myform");
	});
});
