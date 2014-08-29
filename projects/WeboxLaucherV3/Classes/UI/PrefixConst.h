
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
#define NOTIFICATIONPANEL_FOCUS_INDICATOR_IMG             "image/select/selectnoti.png"
//------------------------------------------------------------------defined for topBar---------------------------------------------
#define NORTIFICATION_HINT_IMG                "image/notification/newnotification.png"
#define NOTIFICATION_COUNT_IMG                "image/notification/noti_mark_%d.png"

#define NETWORK_DISABLE_IMG                     "image/wifi/network_disable.png"
#define NETWORK_NET_IMG                         "image/wifi/network_eth.png"
#define NETWORK_WIFI_IMG                        "image/wifi/network_wifi_%d.png"

//-------------------------------------------------------------define for cibn----------------------------------------------
#define CIBN_AUTH_BEGIN_TXT                       "正在向CIBN互联网电视认证..."
#define CIBN_AUTH_END_SUCC_TXT                    "认证成功"
#define CIBN_AUTH_END_FAILE_TXT                   "认证失败"
#define CIBN_AUTH_END_FAILE_NO_VODIE_TXT          "认证失败,无法观看影视内容"

//-------------------------------------------------------------define for Left Notification----------------------------------------------
#define CODE_TV_INFO                              0
#define CODE_VOD_INFO                             1
#define CODE_HISTORY_INFO                         2
#define CODE_ALBUM_INFO                           3
#define CODE_SYSTEM_UPGRADE                       10
#define CODE_CHASE_DRAMA                          11
#define CODE_MOUNT_UNMOUNT                        12
#define CODE_AIRPLAY_INFO                         13
#define CODE_AIRPLAY_STATUS                       14
#define CODE_LIVE_TV                              20
#define CODE_LIVE_VIDEO                           21
#define CODE_LIVE_ALBUM                           22
#define CODE_LIVE_FAVOR                           23

#define EVENT_LIVE_TV                             Name_item_tv
#define EVENT_LIVE_VIDEO                          Name_item_video
#define EVENT_LIVE_ALBUM                          Name_item_album
#define EVENT_LIVE_FAVOR                          Name_item_history
#define Name_item_tv                              "item_tv_name"
#define Name_item_video                           "item_video_name"
#define Name_item_album                           "item_album_name"
#define Name_item_history                         "item_history_name"

#define EVENT_TV_INFO                             "event_tv_info"
#define EVENT_VOD_INFO                            "event_video_info"
#define EVENT_HISTORY_INFO                        "event_history_info"
#define EVENT_ALBUM_INFO                          "event_album_info"
#define EVENT_SYSTEM_UPGRADE                      "event_system_upgrade"
#define EVENT_CHASE_DRAMA                         "event_chase_drama"
#define EVENT_MOUNT_UNMOUNT                       "event_mount_unmount"
#define EVENT_UPDATE_IMAGE_NOTIFY                 "event_update_image"
#define EVENT_UPDATE_BACKGROUND_NOTIFY            "event_update_background"
#define EVENT_UPDATE_RELOAD                       "event_update_reload_app"
#define EVENT_UPDATE_ALLAPPS                      "event_update_all_apps"
#define EVENT_UPDATE_APPS                         "event_update_apps"
#define EVENT_NETWORK_STATE                       "event_network_state_changed"
#define EVENT_DATE_TIME                           "event_date_time_changed"
#define EVENT_Time_Refresh                        "event_time_refresh"

#define BANNER_Background_Image                    "image/notification/BG_up.png"
#define Left_Notificaiton_Background_Image         "image/notification/BG_left.png"
#define Left_UpdateIcon_Image                      "image/notification/ic_update.png"
#define Left_USBIcon_Image                         "image/notification/ic_usb.png"
#define Left_LikeIcon_Image                        "image/notification/ic_like.png"
#define Left_NewNofi_image                         "image/notification/notification_normal.png"
#define Left_Line_image                            "image/notification/notification_line.png"
#define MarkNotification_Icon_Image                "image/notification/newnotification.png"
#define MarkNotification_Mark_image                "image/notification/noti_mark_%d.png"

#define Left_Item_power_Title                      "遥控器电力不足!"
#define Left_Item_USB_Insert_Title                 "U 盘已插入"
#define Left_Item_USB_Pullout_Title                "U 盘已拔出"
#define Left_Item_Update_Title                     "系统有更新"
#define Left_Item_Like_Title                       "剧集有更新"
#define Left_Item_Like_Bann_Title                  "喜欢的剧集有更新"

//-------------------------------------------------------------define for Airplay----------------------------------------------
#define AirPlay_Background_image                  "image/airplay/airbackground.png"
#define AirPlay_MusicMark_image                   "image/airplay/musicicon.png"
#define AirPlay_Icon                              "image/airplay/airplayicon.png"

#define AIRPLAY_DEFAULT_TITLE                      "未知歌名"
#define AIRPLAY_DEFAULT_ALBUM                      "未知专辑"
#define AIRPLAY_DEFAULT_ARTIST                     "未知歌手"

//-------------------------------------------------------------define for ClearMemory----------------------------------------------

#define APPITEM_CLEAN_Img                   "image/appitem/app_clean.png"
#define APPITEM_CLEAN_Img_Sm               "image/appitem/app_clean_2.png"
#define CLEARN_MEMORY_NOTI                       "clearn_memory_noti"

#define APP_ClEARN_CLS                    "com.togic.taskclean.MainActivity"
#define APP_CLEARN_PACKAGE              "com.togic.taskclean"
#define APP_CLEARN_TITLE                    "一键加速"

#define AppItem_bottomBar_Position    Vec2(130,30)
#define DEFALUT_APPCOUNT_OFFSET 2



#define JNI_params_is_entrance                    "intent.extra.is_entrance_actvitiy"
#define JNI_params_exit_directly                  "intent.extra.exit_directly"
#define JNI_params_hide_splash                    "intent.extra.show_splash"

#pragma mark ---- JNI Action
#define JNI_HuNanTV_Action                        "com.starcor.hunan.mgtv"
#define JNI_TV_Action                             "togic.intent.action.LIVE_TV"
#define JNI_Video_Action                          "togic.intent.action.ONLINE_VIDEO"
#define JNI_Album_Action                          "togic.intent.action.ALBUM"
#define JNI_My_favor_Action                       "togic.intent.action.LIVE_VIDEO_PROGRAM_MY_FAVOR"





typedef enum {
     AP_START    = 0x10,
     AP_PLAY     = 0x20,
     AP_PASUE    = 0x30,
     AP_FASTF    = 0x40,
     AP_STOP     = 0x50,
     AP_INFO     = 0x60,
     AP_STOPSLD  = 0x70,
     AP_STARTSLD = 0x80,
     AP_VOLUME   = 0x90,
     AP_COVT     = 0xa0,
     AP_META     = 0xb0,
     AP_ERROR    = 0xff
}kAirplayStruct;


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

#define ACTION_DURATION_TIME  0.15


#define USER_SHOW_TV_KEY  "use_tv_key"







#include                                       "jni/Java_org_cocos2dx_lib_Cocos2dxHelper.h"

#define CIBN_NOTI_NAME                            "cibn_notify_name"
#define CIBN_CODE_KEY                             "cibn_code"

#define CIBN_RESULT_NOTI                          "cibn_result_hint_video"
#define CIBN_RESULT_KEY                           "cibn_result"
#define CIBN_RESUT_NOCLCK_NOTI                    "cibn_result_noclick_noti"

#define MarkNotification_Mark_image                "image/notification/noti_mark_%d.png"

#endif
