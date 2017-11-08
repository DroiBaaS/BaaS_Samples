# Droi\_Sample\_Push

`Droi_Sample_Push`是個簡單的`Android Studio`專案，介紹`DroiBaaS`下的`Push`功能。

## 專案架構

![architecture][img_arch]

### Fragments

專案使用 Fragment 架構，共有三個 Fragment，以下是詳細介紹。

***MainFragment***

主要進入點，底下套用 PagerView。

***PushMainFragment***

Push 主要的 Fragment。顯示目前的裝置 ID (DeviceID) 和標籤。

***PushSetupFragment***

設定 Push 功能，有三種。啟用 PUSH，設定靜默時間，設定標籤。

### ButterKnife

使用 ButterKnife Library 進行 View binding，請參考[官方網頁][link_bk]。

[img_arch]: ./doc/img/img_arch.png
[link_bk]: http://jakewharton.github.io/butterknife/

## 適用情境

展示 Droi Push 功能

## DroiBaaS API

### DroiPush

請參照 [DroiPush 說明][link_push]

[link_push]: https://www.droibaas.com/html/doc_24131.html

### DroiError

`DroiError`為`DroiBaaS`核心 API 之一，表示了 API 呼叫結果，可簡單用`isOk()`判斷 API 有無成功。另外可用`getCode()`取回錯誤代碼。`0`表示成功，其他表示失敗。如果要知道詳細訊息，可用`toString()`印出完整訊息。

## 建置和執行

直接用`Android Studio 3`打開即可，需注意的是，要設定`AndroidManifest.xml`裡的 DroiBaaS Application ID

![App ID][img_app_id]

`Application ID`可至`DroiBaaS`開發者平台中的應用設置->安全密鑰裡查詢

[img_app_id]: ./doc/img/img_app_id.png
[img_web_app_id]: ./doc/img/img_web_app_id.png

## 執行畫面

![Screenshot 1][img_screen1]

![Screenshot 2][img_screen2]

[img_screen1]: ./doc/img/screen1.png
[img_screen2]: ./doc/img/screen2.png