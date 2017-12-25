const MASTER_KEY = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
const TABLE_SHOP_WINDOW = "ShopWindow";
const TABLE_CART = "Cart";
const TABLE_FIELD_ITEMID = "ItemId";
const TABLE_FIELD_NAME = "Name";
const TABLE_FIELD_NOTE = "Note";
const TABLE_FIELD_PRICE = "Price";
const TABLE_FIELD_QUANTITY = "Quantity";
const TABLE_FIELD_CREATIONTIME = "_CreationTime";

const UTIL = require("util.js");

const CONST_SECOND_A_DAY = 86400 * 1000;
const CONST_SECOND_A_HOUR = 3600 * 1000;
const CONST_SECOND_A_MIN = 60 * 1000;

exports.main = function (request, response) {
try{
    var body = { "min": 0, "hour": 0, "day": 1 }
    if (request.body != undefined && request.body != null && request.body != "") {
        body = JSON.parse(request.body);
    }

    Droi.info("Clear item which last "+body.day + " day(s)" + body.hour + " hour(s)" + body.min + " min(s) in cart.");

    var expiredDuration = body.min * CONST_SECOND_A_MIN +
        body.hour * CONST_SECOND_A_HOUR +
        body.day * CONST_SECOND_A_DAY

    var checkExpired = function (item) {

        var nowTime = new Date().getTime();

        var creationTime = item[TABLE_FIELD_CREATIONTIME];
        var timestamp = Date.parse(creationTime);

        var itemInCartElapsed = nowTime - timestamp;
        if (itemInCartElapsed < expiredDuration) {
            Droi.info("item: "+ item._Id + " is not expired.")
            processedCount++;
            if (processedCount == cartItemLength) {
                response.success("finish");
            }
            return;
        }

        Droi.info("item: "+ item._Id + " is already expired.")
        item = Droi.Object(TABLE_CART, MASTER_KEY, item);
        item.destroy().events.on("data", function (res) {
            if (res.isSuccess() != true) {
                response.success("destroy fails. code: " + res.getCode() + "msg:" + res.getMessage());
                processedCount++;
                if (processedCount == cartItemLength) {
                    response.success("finish");
                }
                return;
            }

            processedCount++;
            if (processedCount == cartItemLength) {
                response.success("finish");
            }
            return;
        });

    }

    var cartItemLength = 0
    var processedCount = 0
    UTIL.getCartItems().then(function (res) {
        var items = res.getResult();
        cartItemLength = items.length;
        if (cartItemLength == 0) {
            response.success("No item in cart.");
            return;
        }

        items.forEach(function (item, index, items) {
            checkExpired(item);
        });

    }).
        catch(function (res) {
            if (res instanceof Error) {
                Droi.error("Get a exception here");
                Droi.error(res);
                response.failed("exception.")
                return;
            }

            response.failed("Get cart item. Code: " + res.getCode() + " Msg: " + res.getMessage());
            return;
        });
    } catch(e) {
        Droi.error(e);
        response.send("Runtime failure.");
    }
}