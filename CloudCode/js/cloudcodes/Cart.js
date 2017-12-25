const MASTER_KEY = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
const TABLE_SHOP_WINDOW = "ShopWindow";
const TABLE_CART = "Cart";
const USER_TABLE = "_User";
const TABLE_FIELD_ITEMID = "ItemId";
const TABLE_FIELD_NAME = "Name";
const TABLE_FIELD_NOTE = "Note";
const TABLE_FIELD_PRICE = "Price";
const TABLE_FIELD_QUANTITY = "Quantity";

const UTIL = require("util.js")

// main function
exports.main = function (request, response) {
    var body = JSON.parse(request.body);
    var userName = body.username;
    var password = body.password;
    var requestedItems = body.items;
    var savedCount = 0;

    Droi.info(userName + " wants to add "+requestedItems.length+ " item(s) into cart.");

    var createCartItemAndSave = function (userObj, requestedItem, inStockItem) {
        if (requestedItem.quantity > inStockItem.Quantity) {
            Droi.error("There is no enough item in stock.");
            response.write("Itemid: " + inStockItem[TABLE_FIELD_ITEMID] + "only have" +
                inStockItem[TABLE_FIELD_QUANTITY] + " in stock.");
            response.end()
            return;
        }


        var cartItem = Droi.Object(TABLE_CART, userObj._Id);
        var acl = Droi.Permission();
        acl.setPublicReadPermission(false);
        acl.setPublicWritePermission(false);
        acl.setUserReadPermission(userObj._Id, true);
        acl.setUserWritePermission(userObj._Id, true);
        cartItem.setPermission(acl)
        cartItem.set(TABLE_FIELD_ITEMID, inStockItem.ItemId);
        cartItem.set(TABLE_FIELD_QUANTITY, requestedItem.quantity);
        cartItem.save().events.on("data", function (res) {
            savedCount++
            if (res.isSuccess() != true) {
                Droi.error("Add item to cart fails. " + JSON.stringify(requestedItem))
                Droi.error("Code: " + res.getCode() + "Msg: " + res.getMessage())
                respnose.write("Add item to cart fails.");
                response.end()
                return;
            }

            Droi.info("Add a item into cart.")
            
            if (requestedItems.length == savedCount) {
                response.write("Add "+savedCount+" items into cart.");
                response.end();
            }
            return;
        });
    }

    var addItemsToCart = function (userObj, items) {
        items.forEach(function (requestedItem, index, requestedItems) {
            var inStockItem = Droi.Object(TABLE_SHOP_WINDOW, MASTER_KEY, requestedItem.itemid)
            inStockItem.fetch().events.on("data", function (res) {
                if (res.isSuccess() != true) {
                    Droi.error("item doesn't exist in "+ TABLE_SHOP_WINDOW+". Itemid" + JSON.stringify(requestedItem));
                    respnose.write("Invalid item exist.")
                    response.end()
                    return;
                }

                createCartItemAndSave(userObj, requestedItem, res.getResult()); 
            })
        });
    }

    UTIL.getUserObjByName(userName).then( (res) => {
        Droi.info(userName + " is verified.")
        addItemsToCart(res.getResult()[0], requestedItems);
        return;
    })
    .catch( (res) => {
        if (res instanceof Error) {
            Droi.error("Get a exception here");
            Droi.error(res);
            response.end();
            return;
        }

        Droi.error("Get user from table fails.")
        response.write(" Get user fails. code: " + res.getCode() + " msg: " + res.getMessage());
        response.end();
        return;
    });

    return;

};

