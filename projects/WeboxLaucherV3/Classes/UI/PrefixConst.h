
#ifndef PREFIX_CONST_H_
#define PREFIX_CONST_H_

#define ITEM_PANEL_SIZE_WIDTH  1200
#define ITEM_PANEL_SIZE_HEIGHT 480
#define ItemPanelSize Size(ITEM_PANEL_SIZE_WIDTH, ITEM_PANEL_SIZE_HEIGHT)
#define MARGIN_LEFT  10
#define MARGIN_TOP  10
#define MARGIN_MIDDLE 30

//#define BOTTOM_PART_SIZE_MAIN  Size(260,143)
#define TOP_PART_SIZE_MAIN Size(260,303)
#define ITEM_SIZE_MAIN Size(260,432)

#define ITEM_SIZE_APP Size(260,196)
#define LONGPRESS_DEFALUTCOUNT 10

#define APPITEM_SET_BG_IMG                          "image/appitem/app_bg_4.png"
#define APPITEM_SET_FG_IMG                      "image/appitem/app_settingicon.png"
#define APP_SET_TITLE                             "设置"
#define APP_SET_NAME                            "com.togic.settings"
#define APPITEM_FILE_BG_IMG                     "image/appitem/app_bg_3.png"
#define APPITEM_FILE_FG_IMG                     "image/appitem/app_fileicon.png"
#define APP_FILE_TITLE                            "文件浏览"
#define APP_FILE_NAME                           "com.togic.filebrowser"

#define APP_DELETE_IMG                          "image/appitem/uninstall.png"

#define NetWork_image_disable                     "image/wifi/network_disable.png"
#define NetWork_image_net                         "image/wifi/network_eth.png"
#define NetWork_image_Wifi                        "image/wifi/network_wifi_%d.png"

#define CIBN_NOTI_NAME                            "cibn_notify_name"
#define CIBN_CODE_KEY                             "cibn_code"
#define CIBN_RESULT_KEY                           "cibn_result"
#define CIBN_AUTH_BEGIN_TXT                       "正在向CIBN互联网电视认证..."
#define CIBN_AUTH_END_SUCC_TXT                    "认证成功"
#define CIBN_AUTH_END_FAILE_TXT                   "认证失败"
#define CIBN_AUTH_END_FAILE_NO_VODIE_TXT          "认证失败,无法观看影视内容"
#define CIBN_RESULT_NOTI                          "cibn_result_hint_video"

#define CIBN_RESUT_NOCLCK_NOTI                    "cibn_result_noclick_noti"

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


#define BANNER_Background_Image                    "image/notification/BG_up.png"
#define Left_Notificaiton_Background_Image         "image/notification/BG_left.png"
#define Left_USBIcon_Image                         "image/notification/ic_usb.png"
#define Left_LikeIcon_Image                        "image/notification/ic_like"
#define Left_NewNofi_image                         "image/notification/notification_normal"
#define LEFT_LINE_IMAGE                            "image/notification/notification_line.png"
#define MarkNotification_Icon_Image                "image/notification/newnotification.png"
#define MarkNotification_Mark_image                "image/notification/noti_mark_%d.png"

#define Left_NewNotification_Title                 "新消息"
#define Left_NewNotification_ZeroText              "(0)"

#define Left_Item_power_Title                      "遥控器电力不足!"
#define Left_Item_USB_Insert_Title                 "U 盘已插入"
#define Left_Item_USB_Pullout_Title                "U 盘已拔出"
#define Left_Item_Update_Title                     "系统有更新"
#define Left_Item_Like_Title                       "剧集有更新"
#define Left_Item_Like_Bann_Title                  "喜欢的剧集有更新"

#endif
