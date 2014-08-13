
#ifndef PREFIX_CONST_H_
#define PREFIX_CONST_H_

#define MAIN_LAYER_BACKGROUND_IMG   "image/launcher/Launcher1_01.jpg"

#define ITEM_SIZE_MAIN Size(260,432)
#define ITEM_SIZE_APP Size(260,196)
#define ITEM_SIZE_NOTIFICATION Size(220,60)

#define ITEM_PANEL_SIZE_WIDTH  1280
#define ITEM_PANEL_SIZE_HEIGHT 720
#define ITEM_PANEL_SIZE Size(ITEM_PANEL_SIZE_WIDTH, ITEM_PANEL_SIZE_HEIGHT)
#define MARGIN_LEFT  40
#define MARGIN_TOP  144
#define MARGIN_MIDDLE 40
                                        //Defaule App Information
#define APPITEM_SET_BG_IMG                          "image/appitem/app_bg_4.png"
#define APPITEM_SET_FG_IMG                      "image/appitem/app_settingicon.png"
#define APP_SET_TITLE                             "设置"
#define APP_SET_NAME                            "com.togic.settings"

#define APPITEM_FILE_BG_IMG                     "image/appitem/app_bg_3.png"
#define APPITEM_FILE_FG_IMG                     "image/appitem/app_fileicon.png"
#define APP_FILE_TITLE                            "文件浏览"
#define APP_FILE_NAME                           "com.togic.filebrowser"

#define APPITEM_APPSTORE_BG_IMG                     "image/appitem/app_bg_0.png"
#define APPITEM_APPSTORE_FG_IMG                     "image/appitem/app_fileicon.png"
#define APP_APPSTORE_TITLE                            "应用商城"
#define APP_APPSTORE_NAME                           "com.togic.appstore"

#define APP_DELETE_IMG                          "image/appitem/uninstall.png"
#define DEFAULT_FOCUS_INDICATOR_IMG             "image/select/selected.png"

//----------------------------------------------defined for Notification Panel-----
#define  LEFT_NOTIFICATION_PANEL_SIZE Size(839, 720)
#define LEFT_NOTIFICATION_BACKGROUND_IMG         "image/notification/BG_left.png"
#define MARK_NOTIFICATION_ICON_IMG                "image/notification/newnotification.png"
//#define LEFT_NEWNOTIFICATION_TITLE                 "新消息\"(%d)\""
#define LEFT_NEWNOTIFICATION_TITLE                 "新消息"
#define LEFT_LINE_IMAGE                            "image/notification/notification_line.png"
//------------------------------------------------------------------defined for topBar---------------------------------------------
#define NORTIFICATION_HINT_IMG                "image/notification/newnotification.png"
#define NOTIFICATION_COUNT_IMG                "image/notification/noti_mark_%d.png"

#define NETWORK_DISABLE_IMG                     "image/wifi/network_disable.png"
#define NETWORK_NET_IMG                         "image/wifi/network_eth.png"
#define NETWORK_WIFI_IMG                        "image/wifi/network_wifi_%d.png"

#define LONGPRESS_DEFALUTCOUNT 10
#define MAX_HEIGHT    1000

typedef enum {
    kNetwork_State_Disable = 0,
    kNetwork_State_Ethernet,
    kNetwork_State_Wifi
}NetworkState;

typedef enum{
	Scroll_to_Right = 1,
	Scroll_to_Left ,
	Scroll_to_up,
	Scroll_to_down
}ScrollDirection;










#define CIBN_NOTI_NAME                            "cibn_notify_name"
#define CIBN_CODE_KEY                             "cibn_code"
#define CIBN_RESULT_KEY                           "cibn_result"
#define CIBN_AUTH_BEGIN_TXT                       "正在向CIBN互联网电视认证..."
#define CIBN_AUTH_END_SUCC_TXT                    "认证成功"
#define CIBN_AUTH_END_FAILE_TXT                   "认证失败"
#define CIBN_AUTH_END_FAILE_NO_VODIE_TXT          "认证失败,无法观看影视内容"
#define CIBN_RESULT_NOTI                          "cibn_result_hint_video"

#define CIBN_RESUT_NOCLCK_NOTI                    "cibn_result_noclick_noti"

#define BANNER_Background_Image                    "image/notification/BG_up.png"

#define Left_USBIcon_Image                         "image/notification/ic_usb.png"
#define Left_LikeIcon_Image                        "image/notification/ic_like"
#define Left_NewNofi_image                         "image/notification/notification_normal"


#define MarkNotification_Mark_image                "image/notification/noti_mark_%d.png"



#define Left_Item_power_Title                      "遥控器电力不足!"
#define Left_Item_USB_Insert_Title                 "U 盘已插入"
#define Left_Item_USB_Pullout_Title                "U 盘已拔出"
#define Left_Item_Update_Title                     "系统有更新"
#define Left_Item_Like_Title                       "剧集有更新"
#define Left_Item_Like_Bann_Title                  "喜欢的剧集有更新"

#endif
