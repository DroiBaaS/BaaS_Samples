const MASTER_KEY = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
const TABLE_SHOP_WINDOW = "ShopWindow";
const TABLE_FIELD_ITEMID = "ItemId";
const TABLE_FIELD_NAME = "Name";
const TABLE_FIELD_NOTE = "Note";
const TABLE_FIELD_PRICE = "Price";
const TABLE_FIELD_QUANTITY = "Quantity";
// main function

exports.main = function (request, response) {

    var body = JSON.parse(request.body);
    var items = body.items;
    var count = 0;
    var saved = 0;
    items.forEach(function (item, index, items) {
        var obj = Droi.Object(TABLE_SHOP_WINDOW, MASTER_KEY)
        var id = obj.getId();
        var name = item.name;
        var note = item.note;
        var price = item.price;
        var quantity = item.quantity
        obj.set(TABLE_FIELD_ITEMID, id);
        obj.set(TABLE_FIELD_NAME, name);
        obj.set(TABLE_FIELD_NOTE, note);
        obj.set(TABLE_FIELD_PRICE, price);
        obj.set(TABLE_FIELD_QUANTITY, quantity);

        obj.save().events.on("data", function (res) {
            count++;

            if (res.isSuccess() != true) {
                Droi.error("Save fails code: "+ res.getCode()+ " Msg: "+ res.getMessage()+
                    "item: "+ JSON.stringify(item));
                if (count == items.length) {
                    response.write("Upload " + count + " item(s). Saved: " + saved)
                    response.end();
                }
                return;
            }

            Droi.info("save item: "+JSON.stringify(item)+" success");
            
            saved++

            if (count == items.length) {
                response.write("Upload " + count + " item(s). Saved: " + saved)
                response.end();
            }
        });
    });
};