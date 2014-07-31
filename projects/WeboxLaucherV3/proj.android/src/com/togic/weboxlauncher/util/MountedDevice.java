package com.togic.weboxlauncher.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import android.os.Environment;
import android.os.storage.IMountService;
import android.os.RemoteException;
import android.os.ServiceManager;

public final class MountedDevice implements Comparable<MountedDevice> {
    private static final String TAG = "MountedDevice";

    public static final String FILE_MOUNTS = "/proc/mounts";
    public static final String DEVICE_PREFIX = "/dev/block/vold/";
    public static final int DEVICE_CATEGORY_ALL = -1;
    public static final int DEVICE_CATEGORY_SDCARD = 0;
    public static final int DEVICE_CATEGORY_UDISK = 1;

    public static String INTERNAL_STORAGE = null;
    static {
        try {
            INTERNAL_STORAGE = Environment.getExternalStorageDirectory()
                    .getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
            INTERNAL_STORAGE = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
        }
    }

    public static final String UDISK_BASE_PATH = "/mnt/storage";
    public static final String UDISK_BASE_PATH1 = "/mnt/usb"; // for compat with
                                                              // mstar platform
    public static final String UDISK_BACKUP_PATH = "/mnt/sd";
    public static final String UDISK_BACKUP_PATH1 = "/storage/external_storage/sd";

    private static IMountService mMntService = null;

    public String device = "";
    public String mountPoint = "";
    public String showName = "";
    private boolean isSDCard = false;
    private boolean isUdisk = false;

    public MountedDevice(String dev, String mntPoint) {
        device = dev;
        mountPoint = mntPoint;
        int idx = mntPoint.lastIndexOf("/");
        showName = idx != -1 ? mntPoint.substring(idx) : mntPoint;
        if (mntPoint != null && mntPoint.equals(INTERNAL_STORAGE)) {
            isSDCard = true;
        } else if (mntPoint != null
                && (mntPoint.startsWith(UDISK_BASE_PATH)
                        || mntPoint.startsWith(UDISK_BASE_PATH1)
                        || mntPoint.startsWith(UDISK_BACKUP_PATH) || mntPoint
                            .startsWith(UDISK_BACKUP_PATH1))) {
            isUdisk = true;
        }
    }

    public boolean isSdcard() {
        return isSDCard && !isUdisk;
    }

    public boolean isUdisk() {
        return !isSDCard && isUdisk;
    }

    public boolean equals(Object o) {
        if (o instanceof MountedDevice) {
            return mountPoint.equals(((MountedDevice) o).mountPoint);
        }
        return super.equals(o);
    }

    public String toString() {
        return device + " " + mountPoint + " " + showName + " " + isSDCard;
    }

    public int compareTo(MountedDevice another) {
        return device.compareTo(another.device);
    }

    public static void getMountedDevicesByCategory(List<MountedDevice> devs,
            int category) {
        if (devs == null) {
            return;
        }
        BufferedReader br = null;
        String line = null;
        try {
            devs.clear();
            br = new BufferedReader(new FileReader(FILE_MOUNTS));
            while ((line = br.readLine()) != null) {
                LogUtil.d(TAG, "read line : " + line);
                if (line.startsWith(DEVICE_PREFIX)) {
                    String[] strs = line.split(" ");
                    // have to check if real mount
                    if (strs != null && strs.length > 2 && isRealMount(strs[1])) {
                        MountedDevice md = new MountedDevice(strs[0], strs[1]);
                        LogUtil.d(TAG, "find a mounted device : " + md);
                        if (category == DEVICE_CATEGORY_ALL) {
                            if (md.isSdcard() || md.isUdisk()) {
                                devs.add(md);
                            }
                        } else if (category == DEVICE_CATEGORY_SDCARD) {
                            if (md.isSdcard()) {
                                devs.add(md);
                            }
                        } else if (category == DEVICE_CATEGORY_UDISK) {
                            if (md.isUdisk()) {
                                devs.add(md);
                            }
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getMountedPointByCategory(List<MountedDevice> devs,
            int category) {
        if (devs == null || !isValidCategory(category)) {
            return "";
        }
        int size = devs.size();
        MountedDevice md = null;
        if (category == DEVICE_CATEGORY_SDCARD) {
            for (int i = size - 1; i >= 0; i--) {
                md = devs.get(i);
                if (md.isSdcard()) {
                    return md.mountPoint;
                }
            }
        } else if (category == DEVICE_CATEGORY_UDISK) {
            for (int i = size - 1; i >= 0; i--) {
                md = devs.get(i);
                if (md.isUdisk()) {
                    return md.mountPoint;
                }
            }
        }
        return "";
    }

    public static int getMountedDevicesCountByCategory(
            List<MountedDevice> devs, int category) {
        if (devs == null || !isValidCategory(category)) {
            return -1;
        }
        int size = devs.size();
        int count = 0;
        if (category == DEVICE_CATEGORY_SDCARD) {
            for (int i = size - 1; i >= 0; i--) {
                if (devs.get(i).isSdcard()) {
                    count++;
                }
            }
        } else if (category == DEVICE_CATEGORY_UDISK) {
            for (int i = size - 1; i >= 0; i--) {
                if (devs.get(i).isUdisk()) {
                    count++;
                }
            }
        } else {
            count = size;
        }
        return count;
    }

    public static MountedDevice getMountedDeviceByPath(String path) {
        if (path == null) {
            return null;
        }
        BufferedReader br = null;
        String line = null;
        try {
            br = new BufferedReader(new FileReader(FILE_MOUNTS));
            while ((line = br.readLine()) != null) {
                LogUtil.d(TAG, "read line : " + line);
                if (line.startsWith(DEVICE_PREFIX)) {
                    String[] strs = line.split(" ");
                    if (strs != null && strs.length > 2 && strs[1].equals(path)
                    // have to check if real mount
                            && isRealMount(strs[1])) {
                        return new MountedDevice(strs[0], strs[1]);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void removeMountedDeviceByPath(List<MountedDevice> devs,
            String path) {
        if (devs == null || path == null) {
            return;
        }
        final int size = devs.size();
        for (int i = size - 1; i >= 0; i--) {
            if (devs.get(i).mountPoint.equals(path)) {
                devs.remove(i);
            }
        }
    }

    public static boolean isRealMount(String path) {
        if (mMntService == null) {
            mMntService = IMountService.Stub.asInterface(ServiceManager
                    .getService("mount"));
        }
        if (mMntService != null) {
            try {
                if (Environment.MEDIA_MOUNTED.equals(mMntService
                        .getVolumeState(path))) {
                    LogUtil.d(TAG, path + " is mounted.");
                    return true;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean isValidCategory(int category) {
        return category == DEVICE_CATEGORY_SDCARD
                || category == DEVICE_CATEGORY_UDISK
                || category == DEVICE_CATEGORY_ALL;
    }
}
