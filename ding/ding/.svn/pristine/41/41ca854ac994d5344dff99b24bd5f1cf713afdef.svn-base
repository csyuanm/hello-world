function approvalStatusUpdate(info){
	var json = JSON.parse(info);
	var obj = nlapiLoadRecord('purchaseorder',json.id,true);
	if(json.demo =='agree'){
		obj.setFieldValue('demo','agree');
	}
	if(json.demo =='refuse'){
		obj.setFieldValue('demo','refuse');
	}
	var id = nlapiSubmitRecord(obj,true);
	
	return id;
}