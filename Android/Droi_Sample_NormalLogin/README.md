# Droi\_Sample\_NormalLogin

`Droi_Sample_NormalLogin`是個簡單的`Android Studio`專案，介紹`DroiBaaS`下的`DroiUser`登入和註冊。

## 專案架構

![architecture][img_arch]

### Fragments

專案使用 Fragment 架構，共有三個 Fragment，以下是詳細介紹。

***MainFragment***

主要進入點，會作目前是否已登入使用者了，如果沒登入的話，就跳轉到`LoginFragment`。

***LoginFragment***

可讓使用者輸入帳號和密碼進行登入，按下註冊按鈕會跳轉到`SignupFragment`。登入成功後會跳轉到`MainFragment`

***SignupFragment***

給第一次用的使用者進行註冊，註冊成功後會跳轉到`MainFragment`

### 側邊選單

點擊左上角選單按鈕或由左往右滑可帶出側邊選單，側邊選單只有一個功能 - 登出。

### ButterKnife

使用 ButterKnife Library 進行 View binding，請參考[官方網頁][link_bk]。

[img_arch]: ./doc/img/img_arch.png
[link_bk]: http://jakewharton.github.io/butterknife/

## 適用情境

設計的 APP 需要辨識使用者，或是需要紀錄使用者資料。舉個例，像是設計大學生課表程式，每個學生的課表都不一致，APP 就需要讓學生能夠登入`DroiUser`來分別紀錄課表。

## DroiBaaS API

### DroiUser

`DroiUser`為`DroiBaaS`的基本核心 Class 之一，被使用在管理／紀錄使用者資料。這 Class 是可被繼承的，所以可以加入開發者所需要的欄位。`DroiUser`提供了以下幾種使用者類型

	* 匿名使用者
		只想暫時儲存使用者資料，APP 移除或清除資料後就無法回復
		
	* 一般使用者（本文介紹）
		使用帳號／密碼作登入
		
	* 第三方登入（目前支持 QQ, Sina, WeChat）
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

***loginInBackground***

使用 Background thread 進行一般使用者登入，結果會以呼叫 Task thread 回傳。如果是在 UI thread 呼叫`loginInBackground`，則就在 UI thread 回傳結果。如果是用`TaskDispatcher`呼叫`loginInBackground`，則會在其 Task thread 回傳。

> 如果是在一般 thread 呼叫`DroiBaaS`之`InBackground`API，因為找不到 Dispatcher，所以會改用 UI thread 回傳結果

``` java
MyUser.loginInBackground(userId, password, MyUser.class, new DroiCallback<DroiUser>() {
    @Override
    public void result(DroiUser droiUser, DroiError droiError) {
        if (droiError.isOk()) {
            // 如果登入成功，跳轉到主要畫面
            return;
        }

        // 登入失敗
        String msg;
        switch (droiError.getCode()) {
            case DroiError.USER_NOT_EXISTS:
                msg = getResources().getString(R.string.user_not_exists, userId);
                break;
            case DroiError.USER_PASSWORD_INCORRECT:
                msg = getResources().getString(R.string.user_wrong_password);
                break;
            default:
                msg = getResources().getString(R.string.login_fail, droiError.toString());
                break;
        }
        // 顯示錯誤訊息 msg
    }
});
```

***signUpInBackground***

使用 Backgroud thread 註冊新使用者。同 loginInBackground 的回傳結果方式。

``` java
// 要註冊新使用者，需 new 出新物件
MyUser user = new MyUser();
user.setUserId(userId);
user.setPassword(password);

// MyUser 新增的欄位 Nickname
user.setNickName(nickname);

user.signUpInBackground(new DroiCallback<Boolean>() {
    @Override
    public void result(Boolean success, DroiError droiError) {
        if (success && droiError.isOk()) {
            // 註冊成功！ 需導回到主要畫面
            return;
        }

        // 註冊失敗
        int code = droiError.getCode();
        String msg;
        switch (code) {
            case DroiError.USER_ALREADY_EXISTS:
                msg = getResources().getString(R.string.user_exists, userId);
                break;
            case DroiError.USER_ALREADY_LOGIN: {
                MyUser curUser = MyUser.getCurrentUser(MyUser.class);
                msg = getResources().getString(R.string.user_already_login, curUser.getUserId());
            }
            default:
                msg = getResources().getString(R.string.signup_fail, droiError.toString());
        }

        Log.e(LOG_TAG, msg);
    }
});
```

### DroiError

`DroiError`為`DroiBaaS`核心 API 之一，表示了 API 呼叫結果，可簡單用`isOk()`判斷 API 有無成功。另外可用`getCode()`取回錯誤代碼。`0`表示成功，其他表示失敗。如果要知道詳細訊息，可用`toString()`印出完整訊息。

## 建置和執行

直接用`Android Studio 3`打開即可，需注意的是，要設定`AndroidManifest.xml`裡的 DroiBaaS Application ID

![App ID][img_app_id]

`Application ID`可至`DroiBaaS`開發者平台中的應用設置->安全密鑰裡查詢

![Web App ID][img_web_app_id]

[img_app_id]: ./doc/img/img_app_id.png
[img_web_app_id]: ./doc/img/img_web_app_id.png

## 執行畫面

![Screenshot 1][img_screen1]

![Screenshot 2][img_screen2]

![Screenshot 3][img_screen3]

![Screenshot 4][img_screen4]

[img_screen1]: ./doc/img/screen1.png
[img_screen2]: ./doc/img/screen2.png
[img_screen3]: ./doc/img/screen3.png
[img_screen4]: ./doc/img/screen4.png