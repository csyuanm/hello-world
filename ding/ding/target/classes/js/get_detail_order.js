function getDetailOrder(id){
	var result = lapiLoadRecord('purchaseorder',id,true);
	return result;
}