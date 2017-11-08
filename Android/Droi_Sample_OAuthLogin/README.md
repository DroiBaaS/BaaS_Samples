# Droi\_Sample\_OAuthLogin

`Droi_Sample_OAuthLogin`是個簡單的`Android Studio`專案，介紹`DroiBaaS`下的`DroiUser`第三方登入（QQ, Sina, WeChat）。

## 專案架構

![architecture][img_arch]

### Fragments

專案使用 Fragment 架構，共有二個 Fragment，以下是詳細介紹。

***MainFragment***

主要進入點，會作目前是否已登入使用者了，如果沒登入的話，就跳轉到`LoginFragment`。

***LoginFragment***

可讓使用者選擇要用那種第三方登入，目前有 QQ，Sina，微信三種。登入成功後會跳轉到`MainFragment`

### 側邊選單

點擊左上角選單按鈕或由左往右滑可帶出側邊選單，側邊選單只有一個功能 - 登出。

### ButterKnife

使用 ButterKnife Library 進行 View binding，請參考[官方網頁][link_bk]。

[img_arch]: ./doc/img/img_arch.png
[link_bk]: http://jakewharton.github.io/butterknife/

## 適用情境

設計的 APP 希望能讓使用者方便登入，即使用常見的第三方帳號。

## DroiBaaS API

### DroiUser

`DroiUser`為`DroiBaaS`的基本核心 Class 之一，被使用在管理／紀錄使用者資料。這 Class 是可被繼承的，所以可以加入開發者所需要的欄位。`DroiUser`提供了以下幾種使用者類型

	* 匿名使用者
		只想暫時儲存使用者資料，APP 移除或清除資料後就無法回復
		
	* 一般使用者
		使用帳號／密碼作登入
		
	* 第三方登入（目前支持 QQ, Sina, WeChat）（本文介紹）
		使用 QQ, Sina, WeChat 作認證登入
		
	* OTP 登入
		使用發送的 OTP code 作認證登入
		
以下本專案所使用`DroiUser`API

***getCurrentUser***

當使用者登入後，會在本地端暫存，隨時都可以呼叫`getCurrentUser`取回目前已登入的使用者。

``` java
public class MyUser extends DroiUser {
    @DroiExpose
    private String nickName;

    public void setNickName(String name) {
        nickName = name;
    }

    public String getNickName() {
        return nickName;
    }
}
```

> MyUser (DroiUser) 也是個 DroiObject，所以需要進行註冊的動作。建議在`Application.onCreate`作註冊。
> DroiObject.registerCustomClass(MyUser.class)

``` java
MyUser curUser = MyUser.getCurrentUser(MyUser.class)
if (curUser == null || !curUser.isLoggedIn()) {
	// 使用者未登入，或是逾期，請導回到登入流程
}
```

***loginOAuthAsync***

使用 Background thread 進行第三方使用者登入，結果會以 UI thread 回傳。

``` java
// Login OAuth callback handler
private DroiCallback<MyUser> loginCallback = new DroiCallback<MyUser>() {
    @Override
    public void result(MyUser myUser, DroiError droiError) {
        // Login fail.
        if (!droiError.isOk()) {
            return;
        }

        // Login OK !
    }
};
    
DroiUser.loginOAuthAsync(getActivity(), provider, loginCallback, MyUser.class);
```

***saveInBackground***

在第三方登入成功後，進行使用者詳細資料填寫。填寫完後，呼叫`saveInBackground`把更動傳回 Server.

``` java
// Login OK !
// Change user nickname here
MyUser myUser = DroiUser.getCurrentUser(MyUser.class);
myUser.setNickName("OAuth User");
myUser.saveInBackground(new DroiCallback<Boolean>() {
    @Override
    public void result(Boolean aBoolean, DroiError droiError) {
        // Update nickname fail.
        if (!droiError.isOk()) {
            return;
        }

        // All login process done !
    }
});
```

### OAuthProvider

`DroiBaaS`中處理第三方登入相關訊息的 API，舉凡選擇哪一種第三方登入，取回第三方 Application ID，登入成功後拿取`Access Token`和`OpenId`。以下介紹常用 API

***fetchOAUthKeysInBackground***

有些第三方登入會需要填入 AppID，可在`DroiBaaS`網頁上填好，在手機端即可用此 API 取回作為登入使用。

在呼叫`loginOAuthAsync`前一定要呼叫本 API 取回 App ID，否則 login 會失敗。

``` java
OAuthProvider.fetchOAUthKeysInBackground(new DroiCallback<Boolean>() {
    @Override
    public void result(Boolean aBoolean, DroiError droiError) {
        // fetch OK.     
    }
});
```
***createAuthProvider***

選擇使用那個第三方登入。

> Tips! 需注意的是`DroiBaaS`預設只獲取最低權限範圍而已，如果需要更大權限範圍，請指定參數 - scope。

``` java
provider = OAuthProvider.createAuthProvider(OAuthProvider.AuthProvider.QQ, getActivity());
```

``` java
provider = OAuthProvider.createAuthProvider(OAuthProvider.AuthProvider.QQ, getActivity(), "all");
```
***getToken***

登入成功後，可呼叫本 API 取回 Access Token

***getId***

登入成功後，可呼叫本 API 取回 OpenId

***getAppId***

取回第三方登入的 Application Id

### DroiError

`DroiError`為`DroiBaaS`核心 API 之一，表示了 API 呼叫結果，可簡單用`isOk()`判斷 API 有無成功。另外可用`getCode()`取回錯誤代碼。`0`表示成功，其他表示失敗。如果要知道詳細訊息，可用`toString()`印出完整訊息。

## 建置和執行

直接用`Android Studio 3`打開即可，需注意的是，要設定`AndroidManifest.xml`裡的 DroiBaaS Application ID

![App ID][img_app_id]

`Application ID`可至`DroiBaaS`開發者平台中的應用設置->安全密鑰裡查詢

![Web App ID][img_web_app_id]

因為 QQ 需要在`AndroidManifest.xml`加入一個特定的`Activity`，且需要填入 QQ AppID，所以請把`[QQ AppID]`換成開發者的 QQ App Id。

[img_app_id]: ./doc/img/img_app_id.png
[img_web_app_id]: ./doc/img/img_web_app_id.png

## 執行畫面

![Screenshot 1][img_screen1]

![Screenshot 3][img_screen3]

![Screenshot 4][img_screen4]

[img_screen1]: ./doc/img/screen1.png
[img_screen3]: ./doc/img/screen3.png
[img_screen4]: ./doc/img/screen4.png