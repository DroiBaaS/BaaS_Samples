const MASTER_KEY = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
const TABLE_SHOP_WINDOW = "ShopWindow";
const TABLE_CART = "Cart";
const TABLE_SOLD = "Sold";
const TABLE_FIELD_ITEMID = "ItemId";
const TABLE_FIELD_NAME = "Name";
const TABLE_FIELD_NOTE = "Note";
const TABLE_FIELD_PRICE = "Price";
const TABLE_FIELD_QUANTITY = "Quantity";

const UTIL = require('util.js');

// main function
exports.main = function (request, response) {
    var body = JSON.parse(request.body);
    var userName = body.username;
    var password = body.password;
    var amount = 0

    Droi.info(userName+ " wants to check bill.");

    var checkBill = function (userObj) {
        var droi_query = Droi.Query(TABLE_CART, userObj._Id);

        droi_query.find().events.on('data', function (res) {
            if (res.isSuccess() == false) {
                Droi.error("Query Cart fails. Code:" + res.getCode(),
                    "Message:" + res.getMessage());
                response.send("Query Cart fails. Code: " + res.getCode() +
                    " Message: " + res.getMessage());
                return;
            }

            var inCartItems = res.getResult();
            var inCartProcessedCount = 0;
            if (inCartItems.length == 0) {
                Droi.info("No item to check");
                response.send("No item to check.");
                return;
            }

            Droi.info("There "+ inCartItems.length + " in "+ userName + "'s cart.");

            inCartItems.forEach(function (inCartItem, index, inCartItems) {
                var cartItemObj = Droi.Object(TABLE_CART, userObj._Id, inCartItem);
                cartItemObj.fetch().events.on("data", function (res) {
                    if (res.isSuccess() != true) {
                        Droi.error("cart itemid: " + cartItemObj);
                        Droi.error("fetch Cart item fails. Code:"+ res.getCode()+
                            "Message:"+ res.getMessage());
                        response.write("cart itemid: " + cartItemObj);
                        response.write("fetch Cart item fails. Code:" + res.getCode() +
                            "Message:" + res.getMessage());
                        inCartProcessedCount++;
                        if (inCartProcessedCount == inCartItems.length) {
                            response.send("finished");
                        }
                        return;
                    }

                    var checkedItem = Droi.Object(TABLE_SOLD, MASTER_KEY);
                    checkedItem.set(TABLE_FIELD_ITEMID, cartItemObj.get(TABLE_FIELD_ITEMID));
                    checkedItem.set(TABLE_FIELD_QUANTITY, cartItemObj.get(TABLE_FIELD_QUANTITY));
                    checkedItem.save().events.on('data', function (res) {
                        if (res.isSuccess() == false) {
                            Droi.error("checkedItem save fails. code:" + res.getCode(),
                                "Message:", res.getMessage());
                            Droi.error("checkedItem: " + JSON.stringify(checkedItem));
                            response.write("checkedItem save fails. code:" + res.getCode() +
                                "Message:" + res.getMessage())
                            inCartProcessedCount++;
                            if (inCartProcessedCount == inCartItems.length) {
                                response.send("Check bill completed. Total cost: "+ amount);
                            }
                            return;
                        }
                    });

                    var inStockItemObj = Droi.Object(TABLE_SHOP_WINDOW, MASTER_KEY, cartItemObj.get(TABLE_FIELD_ITEMID));
                    inStockItemObj.fetch().events.on("data", function (res) {
                        if (res.isSuccess() != true) {
                            Droi.error("fetch " + TABLE_SHOP_WINDOW + " fails. code:", res.getCode(),
                                "Message:", res.getMessage());
                            Droi.error("itemid: " + cartItemObj.get(TABLE_FIELD_ITEMID));
                            Droi.info("inCartProcessedCount: " + inCartProcessedCount);
                            inCartProcessedCount++
                            if (inCartProcessedCount == inCartItems.length) {
                                response.send("Check bill completed. Total cost: "+ amount);
                            }
                            return;
                        }

                        amount = amount + cartItemObj.get(TABLE_FIELD_QUANTITY) * inStockItemObj.get(TABLE_FIELD_PRICE);

                        var purchasedCount = -cartItemObj.get(TABLE_FIELD_QUANTITY)
                        inStockItemObj.increment(TABLE_FIELD_QUANTITY, purchasedCount);
                        var itemName = inStockItemObj.get(TABLE_FIELD_NAME)
                        inStockItemObj.save().events.on("data", function (res) {
                            if (res.isSuccess() != true) {
                                Droi.error("save " + TABLE_SHOP_WINDOW + " fails. code:", res.getCode(),
                                    "Message:", res.getMessage());
                                Droi.error("itemid: " + inStockItemObj.getId());
                                Droi.error("table: " + inStockItemObj.getTableName());

                                inCartProcessedCount++
                                if (inCartProcessedCount == inCartItems.length) {
                                    response.send("Check bill completed. Total cost: "+ amount);
                                }
                                return;
                            }

                            Droi.info("Item: " +cartItemObj.getId() + " has been checked.");
                            cartItemObj.destroy().events.on("data", function (res) {
                                if (res.isSuccess() != true) {
                                    Droi.error("destroy " + TABLE_CART + " fails. code:", res.getCode(),
                                        "Message:", res.getMessage());
                                    Droi.error("itemid: " + cartItemObj.getId());
                                }

                                if (inCartProcessedCount == inCartItems.length) {
                                    response.send("Check bill completed. Total cost: "+ amount);
                                }
                            });

                            inCartProcessedCount++
                            Droi.info("inCartProcessedCount: " + inCartProcessedCount);
                        });
                    });

                });

            });
        });
    }


    UTIL.getUserObjByName(userName).then((res) => {
        Droi.info(userName + " is verified.")
        checkBill(res.getResult()[0]);
        return;
    })
        .catch((res) => {
            if (res instanceof Error) {
                Droi.error("Get a exception here");
                Droi.error(res);
                response.send("finished");
                return;
            }

            response.write(" Get user fails. code: " + res.getCode() + " msg: " + res.getMessage());
            response.end();
            return;
        });

};