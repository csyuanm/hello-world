function getDetailOrder(id){
	var result = lapiLoadRecord('expenseorder',id,true);
	return result;
}