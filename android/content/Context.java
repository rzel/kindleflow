/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.content;


import android.content.res.Resources;



/**
 * Interface to global information about an application environment.  This is
 * an abstract class whose implementation is provided by
 * the Android system.  It
 * allows access to application-specific resources and classes, as well as
 * up-calls for application-level operations such as launching activities,
 * broadcasting and receiving intents, etc.
 */
public abstract class Context {
    /**
     * File creation mode: the default mode, where the created file can only
     * be accessed by the calling application (or all applications sharing the
     * same user ID).
     * @see #MODE_WORLD_READABLE
     * @see #MODE_WORLD_WRITEABLE
     */
    public static final int MODE_PRIVATE = 0x0000;
    /**
     * File creation mode: allow all other applications to have read access
     * to the created file.
     * @see #MODE_PRIVATE
     * @see #MODE_WORLD_WRITEABLE
     */
    public static final int MODE_WORLD_READABLE = 0x0001;
    /**
     * File creation mode: allow all other applications to have write access
     * to the created file.
     * @see #MODE_PRIVATE
     * @see #MODE_WORLD_READABLE
     */
    public static final int MODE_WORLD_WRITEABLE = 0x0002;
    /**
     * File creation mode: for use with {@link #openFileOutput}, if the file
     * already exists then write data to the end of the existing file
     * instead of erasing it.
     * @see #openFileOutput
     */
    public static final int MODE_APPEND = 0x8000;

    /**
     * Flag for {@link #bindService}: automatically create the service as long
     * as the binding exists.  Note that while this will create the service,
     * its {@link android.app.Service#onStart} method will still only be called due to an
     * explicit call to {@link #startService}.  Even without that, though,
     * this still provides you with access to the service object while the
     * service is created.
     *
     * <p>Specifying this flag also tells the system to treat the service
     * as being as important as your own process -- that is, when deciding
     * which process should be killed to free memory, the service will only
     * be considered a candidate as long as the processes of any such bindings
     * is also a candidate to be killed.  This is to avoid situations where
     * the service is being continually created and killed due to low memory.
     */
    public static final int BIND_AUTO_CREATE = 0x0001;

    /**
     * Flag for {@link #bindService}: include debugging help for mismatched
     * calls to unbind.  When this flag is set, the callstack of the following
     * {@link #unbindService} call is retained, to be printed if a later
     * incorrect unbind call is made.  Note that doing this requires retaining
     * information about the binding that was made for the lifetime of the app,
     * resulting in a leak -- this should only be used for debugging.
     */
    public static final int BIND_DEBUG_UNBIND = 0x0002;



    /** Return a Resources instance for your application's package. */
    public abstract Resources getResources();




    
    /**
     * Return the context of the single, global Application object of the
     * current process.
     */
    public abstract Context getApplicationContext();
    
    /** XXX
     * Return a localized, styled CharSequence from the application's package's
     * default string table.
     *
     * @param resId Resource id for the CharSequence text
     
    public final String getText(int resId) {
        return getResources().getText(resId);
    }*/

    /**
     * Return a localized string from the application's package's
     * default string table.
     *
     * @param resId Resource id for the string
     */
    public final String getString(int resId) {
        return getResources().getString(resId);
    }

    /** xxx
     * Return a localized formatted string from the application's package's
     * default string table, substituting the format arguments as defined in
     * {@link java.util.Formatter} and {@link java.lang.String#format}.
     *
     * @param resId Resource id for the format string
     * @param formatArgs The format arguments that will be used for substitution.
    

    public final String getString(int resId, Object... formatArgs) {
        return getResources().getString(resId, formatArgs);
    } */

     /**
     * Set the base theme for this context.  Note that this should be called
     * before any views are instantiated in the Context (for example before
     * calling {@link android.app.Activity#setContentView} or
     * {@link android.view.LayoutInflater#inflate}).
     *
     * @param resid The style resource describing the theme.
     */
    public abstract void setTheme(int resid);

    /**
     * Return the Theme object associated with this Context.
     */
    public abstract Resources.Theme getTheme();

    /** xxx
     * Retrieve styled attribute information in this Context's theme.  See
     * {@link Resources.Theme#obtainStyledAttributes(int[])}
     * for more information.
     *
     * @see Resources.Theme#obtainStyledAttributes(int[])
   
    public final TypedArray obtainStyledAttributes(int[] attrs) {
        return getTheme().obtainStyledAttributes(attrs);
    }  */

    /** xxx
     * Retrieve styled attribute information in this Context's theme.  See
     * {@link Resources.Theme#obtainStyledAttributes(int, int[])}
     * for more information.
     *
     * @see Resources.Theme#obtainStyledAttributes(int, int[])
     
    public final TypedArray obtainStyledAttributes(
            int resid, int[] attrs) throws Resources.NotFoundException {
        return getTheme().obtainStyledAttributes(resid, attrs);
    }*/

    /** xxx
     * Retrieve styled attribute information in this Context's theme.  See
     * {@link Resources.Theme#obtainStyledAttributes(AttributeSet, int[], int, int)}
     * for more information.
     *
     * @see Resources.Theme#obtainStyledAttributes(AttributeSet, int[], int, int)
    
    public final TypedArray obtainStyledAttributes(AttributeSet set, int[] attrs) {
        return getTheme().obtainStyledAttributes(set, attrs, 0, 0);
    } */

    /** xxx
     * Retrieve styled attribute information in this Context's theme.  See
     * {@link Resources.Theme#obtainStyledAttributes(AttributeSet, int[], int, int)}
     * for more information.
     *
     * @see Resources.Theme#obtainStyledAttributes(AttributeSet, int[], int, int)
    
    public final TypedArray obtainStyledAttributes(AttributeSet set, int[] attrs, int defStyleAttr, int defStyleRes) {
        return getTheme().obtainStyledAttributes( set, attrs, defStyleAttr, defStyleRes);
    } */



    /** Return the name of this application's package. */
    public abstract String getPackageName();

    /**
     * {@hide}
     * Return the full path to this context's resource files.  This is the ZIP files
     * containing the application's resources.
     *
     * <p>Note: this is not generally useful for applications, since they should
     * not be directly accessing the file system.
     *
     *
     * @return String Path to the resources.
     */
    public abstract String getPackageResourcePath();

    /**
     * {@hide}
     * Return the full path to this context's code and asset files.  This is the ZIP files
     * containing the application's code and assets.
     *
     * <p>Note: this is not generally useful for applications, since they should
     * not be directly accessing the file system.
     *
     *
     * @return String Path to the code and assets.
     */
    public abstract String getPackageCodePath();

    /**
     * Retrieve and hold the contents of the preferences file 'name', returning
     * a SharedPreferences through which you can retrieve and modify its
     * values.  Only one instance of the SharedPreferences object is returned
     * to any callers for the same name, meaning they will see each other's
     * edits as soon as they are made.
     *
     * @param name Desired preferences file. If a preferences file by this name
     * does not exist, it will be created when you retrieve an
     * editor (SharedPreferences.edit()) and then commit changes (Editor.commit()).
     * @param mode Operating mode.  Use 0 or {@link #MODE_PRIVATE} for the
     * default operation, {@link #MODE_WORLD_READABLE}
     * and {@link #MODE_WORLD_WRITEABLE} to control permissions.
     *
     * @return Returns the single SharedPreferences instance that can be used
     *         to retrieve and modify the preference values.
     *
     * @see #MODE_PRIVATE
     * @see #MODE_WORLD_READABLE
     * @see #MODE_WORLD_WRITEABLE
     */
  //XXX public abstract SharedPreferences getSharedPreferences(String name, int mode);

    /**
     * Open a private file associated with this Context's application package
     * for reading.
     *
     * @param name The name of the file to open; can not contain path
     *             separators.
     *
     * @return FileInputStream Resulting input stream.
     *
     * @see #openFileOutput
     * @see #fileList
     * @see #deleteFile
     * @see java.io.FileInputStream#FileInputStream(String)
     */
  //XXX public abstract FileInputStream openFileInput(String name) throws FileNotFoundException;

    /**
     * Open a private file associated with this Context's application package
     * for writing.  Creates the file if it doesn't already exist.
     *
     * @param name The name of the file to open; can not contain path
     *             separators.
     * @param mode Operating mode.  Use 0 or {@link #MODE_PRIVATE} for the
     * default operation, {@link #MODE_APPEND} to append to an existing file,
     * {@link #MODE_WORLD_READABLE} and {@link #MODE_WORLD_WRITEABLE} to control
     * permissions.
     *
     * @return FileOutputStream Resulting output stream.
     *
     * @see #MODE_APPEND
     * @see #MODE_PRIVATE
     * @see #MODE_WORLD_READABLE
     * @see #MODE_WORLD_WRITEABLE
     * @see #openFileInput
     * @see #fileList
     * @see #deleteFile
     * @see java.io.FileOutputStream#FileOutputStream(String)
     */
  //XXX  public abstract FileOutputStream openFileOutput(String name, int mode) throws FileNotFoundException;

    /**
     * Delete the given private file associated with this Context's
     * application package.
     *
     * @param name The name of the file to delete; can not contain path
     *             separators.
     *
     * @return True if the file was successfully deleted; else
     *         false.
     *
     * @see #openFileInput
     * @see #openFileOutput
     * @see #fileList
     * @see java.io.File#delete()
     */
    public abstract boolean deleteFile(String name);

    /**
     * Returns the absolute path on the filesystem where a file created with
     * {@link #openFileOutput} is stored.
     *
     * @param name The name of the file for which you would like to get
     *          its path.
     *
     * @return Returns an absolute path to the given file.
     *
     * @see #openFileOutput
     * @see #getFilesDir
     * @see #getDir
     */
  //XXX  public abstract File getFileStreamPath(String name);

    /**
     * Returns the absolute path to the directory on the filesystem where
     * files created with {@link #openFileOutput} are stored.
     *
     * @return Returns the path of the directory holding application files.
     *
     * @see #openFileOutput
     * @see #getFileStreamPath
     * @see #getDir
     */
  //XXX public abstract File getFilesDir();
    
    /**
     * Returns the absolute path to the application specific cache directory 
     * on the filesystem. These files will be ones that get deleted first when the
     * device runs low on storage
     * There is no guarantee when these files will be deleted.
     *
     * @return Returns the path of the directory holding application cache files.
     *
     * @see #openFileOutput
     * @see #getFileStreamPath
     * @see #getDir
     */
  //XXX public abstract File getCacheDir();

    /**
     * Returns an array of strings naming the private files associated with
     * this Context's application package.
     *
     * @return Array of strings naming the private files.
     *
     * @see #openFileInput
     * @see #openFileOutput
     * @see #deleteFile
     */
  //XXX  public abstract String[] fileList();

    /**
     * Retrieve, creating if needed, a new directory in which the application
     * can place its own custom data files.  You can use the returned File
     * object to create and access files in this directory.  Note that files
     * created through a File object will only be accessible by your own
     * application; you can only set the mode of the entire directory, not
     * of individual files.
     *
     * @param name Name of the directory to retrieve.  This is a directory
     * that is created as part of your application data.
     * @param mode Operating mode.  Use 0 or {@link #MODE_PRIVATE} for the
     * default operation, {@link #MODE_WORLD_READABLE} and
     * {@link #MODE_WORLD_WRITEABLE} to control permissions.
     *
     * @return Returns a File object for the requested directory.  The directory
     * will have been created if it does not already exist.
     *
     * @see #openFileOutput(String, int)
     */
  //XXX public abstract File getDir(String name, int mode);

    /**
     * Open a new private SQLiteDatabase associated with this Context's
     * application package.  Create the database file if it doesn't exist.
     *
     * @param name The name (unique in the application package) of the database.
     * @param mode Operating mode.  Use 0 or {@link #MODE_PRIVATE} for the
     *     default operation, {@link #MODE_WORLD_READABLE}
     *     and {@link #MODE_WORLD_WRITEABLE} to control permissions.
     * @param factory An optional factory class that is called to instantiate a
     *     cursor when query is called.
     *
     * @return The contents of a newly created database with the given name.
     * @throws SQLiteException if the database file could not be opened.
     *
     * @see #MODE_PRIVATE
     * @see #MODE_WORLD_READABLE
     * @see #MODE_WORLD_WRITEABLE
     * @see #deleteDatabase
     */
  //XXX public abstract SQLiteDatabase openOrCreateDatabase(String name,  int mode, CursorFactory factory);

    /**
     * Delete an existing private SQLiteDatabase associated with this Context's
     * application package.
     *
     * @param name The name (unique in the application package) of the
     *             database.
     *
     * @return True if the database was successfully deleted; else false.
     *
     * @see #openOrCreateDatabase
     */
    //XXX public abstract boolean deleteDatabase(String name);

    /**
     * Returns the absolute path on the filesystem where a database created with
     * {@link #openOrCreateDatabase} is stored.
     *
     * @param name The name of the database for which you would like to get
     *          its path.
     *
     * @return Returns an absolute path to the given database.
     *
     * @see #openOrCreateDatabase
     */
  //XXX public abstract File getDatabasePath(String name);

    /**
     * Returns an array of strings naming the private databases associated with
     * this Context's application package.
     *
     * @return Array of strings naming the private databases.
     *
     * @see #openOrCreateDatabase
     * @see #deleteDatabase
     */
    public abstract String[] databaseList();






    /**
     * Return the handle to a system-level service by name.  The class of the
     * returned object varies by the requested name.  Currently available names
     * are:
     *
     * <dl>
     *  <dt> {@link #WINDOW_SERVICE} ("window")
     *  <dd> The top-level window manager in which you can place custom
     *  windows.  The returned object is a {@link android.view.WindowManager}.
     *  <dt> {@link #LAYOUT_INFLATER_SERVICE} ("layout_inflater")
     *  <dd> A {@link android.view.LayoutInflater} for inflating layout resources
     *  in this context.
     *  <dt> {@link #ACTIVITY_SERVICE} ("activity")
     *  <dd> A {@link android.app.ActivityManager} for interacting with the
     *  global activity state of the system.
     *  <dt> {@link #POWER_SERVICE} ("power")
     *  <dd> A {@link android.os.PowerManager} for controlling power
     *  management.
     *  <dt> {@link #ALARM_SERVICE} ("alarm")
     *  <dd> A {@link android.app.AlarmManager} for receiving intents at the
     *  time of your choosing.
     *  <dt> {@link #NOTIFICATION_SERVICE} ("notification")
     *  <dd> A {@link android.app.NotificationManager} for informing the user
     *   of background events.
     *  <dt> {@link #KEYGUARD_SERVICE} ("keyguard")
     *  <dd> A {@link android.app.KeyguardManager} for controlling keyguard.
     *  <dt> {@link #LOCATION_SERVICE} ("location")
     *  <dd> A {@link android.location.LocationManager} for controlling location
     *   (e.g., GPS) updates.
     *  <dt> {@link #SEARCH_SERVICE} ("search")
     *  <dd> A {@link android.app.SearchManager} for handling search.
     *  <dt> {@link #VIBRATOR_SERVICE} ("vibrator")
     *  <dd> A {@link android.os.Vibrator} for interacting with the vibrator
     *  hardware.
     *  <dt> {@link #CONNECTIVITY_SERVICE} ("connection")
     *  <dd> A {@link android.net.ConnectivityManager ConnectivityManager} for
     *  handling management of network connections.
     *  <dt> {@link #WIFI_SERVICE} ("wifi")
     *  <dd> A {@link android.net.wifi.WifiManager WifiManager} for management of
     * Wi-Fi connectivity.
     * </dl>
     * 
     * <p>Note:  System services obtained via this API may be closely associated with
     * the Context in which they are obtained from.  In general, do not share the
     * service objects between various different contexts (Activities, Applications,
     * Services, Providers, etc.)
     *
     * @param name The name of the desired service.
     *
     * @return The service or null if the name does not exist.
     *
     * @see #WINDOW_SERVICE
     * @see android.view.WindowManager
     * @see #LAYOUT_INFLATER_SERVICE
     * @see android.view.LayoutInflater
     * @see #ACTIVITY_SERVICE
     * @see android.app.ActivityManager
     * @see #POWER_SERVICE
     * @see android.os.PowerManager
     * @see #ALARM_SERVICE
     * @see android.app.AlarmManager
     * @see #NOTIFICATION_SERVICE
     * @see android.app.NotificationManager
     * @see #KEYGUARD_SERVICE
     * @see android.app.KeyguardManager
     * @see #LOCATION_SERVICE
     * @see android.location.LocationManager
     * @see #SEARCH_SERVICE
     * @see android.app.SearchManager
     * @see #SENSOR_SERVICE
     * @see android.hardware.SensorManager
     * @see #VIBRATOR_SERVICE
     * @see android.os.Vibrator
     * @see #CONNECTIVITY_SERVICE
     * @see android.net.ConnectivityManager
     * @see #WIFI_SERVICE
     * @see android.net.wifi.WifiManager
     * @see #AUDIO_SERVICE
     * @see android.media.AudioManager
     * @see #TELEPHONY_SERVICE
     * @see android.internal.TelephonyManager
     */
    public abstract Object getSystemService(String name);

    /**
     * Use with {@link #getSystemService} to retrieve a
     * {@link android.os.PowerManager} for controlling power management,
     * including "wake locks," which let you keep the device on while
     * you're running long tasks.
     */
    public static final String POWER_SERVICE = "power";
    /**
     * Use with {@link #getSystemService} to retrieve a
     * {@link android.view.WindowManager} for accessing the system's window
     * manager.
     *
     * @see #getSystemService
     * @see android.view.WindowManager
     */
    public static final String WINDOW_SERVICE = "window";
    /**
     * Use with {@link #getSystemService} to retrieve a
     * {@link android.view.LayoutInflater} for inflating layout resources in this
     * context.
     *
     * @see #getSystemService
     * @see android.view.LayoutInflater
     */
    public static final String LAYOUT_INFLATER_SERVICE = "layout_inflater";
    /**
     * Use with {@link #getSystemService} to retrieve a
     * {@link android.app.ActivityManager} for interacting with the global
     * system state.
     *
     * @see #getSystemService
     * @see android.app.ActivityManager
     */
    public static final String ACTIVITY_SERVICE = "activity";
    /**
     * Use with {@link #getSystemService} to retrieve a
     * {@link android.app.AlarmManager} for receiving intents at a
     * time of your choosing.
     *
     * @see #getSystemService
     * @see android.app.AlarmManager
     */
    public static final String ALARM_SERVICE = "alarm";
    /**
     * Use with {@link #getSystemService} to retrieve a
     * {@link android.app.NotificationManager} for informing the user of
     * background events.
     *
     * @see #getSystemService
     * @see android.app.NotificationManager
     */
    public static final String NOTIFICATION_SERVICE = "notification";
    /**
     * Use with {@link #getSystemService} to retrieve a
     * {@link android.app.NotificationManager} for controlling keyguard.
     *
     * @see #getSystemService
     * @see android.app.KeyguardManager
     */
    public static final String KEYGUARD_SERVICE = "keyguard";
    /**
     * Use with {@link #getSystemService} to retrieve a {@link
     * android.location.LocationManager} for controlling location
     * updates.
     *
     * @see #getSystemService
     * @see android.location.LocationManager
     */
    public static final String LOCATION_SERVICE = "location";
    /**
     * Use with {@link #getSystemService} to retrieve a {@link
     * android.app.SearchManager} for handling searches.
     *
     * @see #getSystemService
     * @see android.app.SearchManager
     */
    public static final String SEARCH_SERVICE = "search";
    /**
     * Use with {@link #getSystemService} to retrieve a {@link
     * android.hardware.SensorManager} for accessing sensors.
     *
     * @see #getSystemService
     * @see android.hardware.SensorManager
     */
    public static final String SENSOR_SERVICE = "sensor";
    /**
     * Use with {@link #getSystemService} to retrieve a {@link
     * android.bluetooth.BluetoothDevice} for interacting with Bluetooth.
     *
     * @see #getSystemService
     * @see android.bluetooth.BluetoothDevice
     * @hide
     */
    public static final String BLUETOOTH_SERVICE = "bluetooth";
    /**
     * Use with {@link #getSystemService} to retrieve a
     * com.android.server.WallpaperService for accessing wallpapers.
     *
     * @see #getSystemService
     */
    public static final String WALLPAPER_SERVICE = "wallpaper";
    /**
     * Use with {@link #getSystemService} to retrieve a {@link
     * android.os.Vibrator} for interacting with the vibration hardware.
     *
     * @see #getSystemService
     * @see android.os.Vibrator
     */
    public static final String VIBRATOR_SERVICE = "vibrator";
    /**
     * Use with {@link #getSystemService} to retrieve a {@link
     * android.app.StatusBarManager} for interacting with the status bar.
     *
     * @see #getSystemService
     * @see android.app.StatusBarManager
     * @hide
     */
    public static final String STATUS_BAR_SERVICE = "statusbar";

    /**
     * Use with {@link #getSystemService} to retrieve a {@link
     * android.net.ConnectivityManager} for handling management of
     * network connections.
     *
     * @see #getSystemService
     * @see android.net.ConnectivityManager
     */
    public static final String CONNECTIVITY_SERVICE = "connectivity";

    /**
     * Use with {@link #getSystemService} to retrieve a {@link
     * android.net.wifi.WifiManager} for handling management of
     * Wi-Fi access.
     *
     * @see #getSystemService
     * @see android.net.wifi.WifiManager
     */
    public static final String WIFI_SERVICE = "wifi";
    
    /**
     * Use with {@link #getSystemService} to retrieve a
     * {@link android.media.AudioManager} for handling management of volume,
     * ringer modes and audio routing.
     * 
     * @see #getSystemService
     * @see android.media.AudioManager
     */
    public static final String AUDIO_SERVICE = "audio";
    
    /**
     * Use with {@link #getSystemService} to retrieve a
     * {@link android.telephony.TelephonyManager} for handling management the
     * telephony features of the device.
     * 
     * @see #getSystemService
     * @see android.telephony.TelephonyManager
     */
    public static final String TELEPHONY_SERVICE = "phone";

    /**
     * Use with {@link #getSystemService} to retrieve a
     * {@link android.text.ClipboardManager} for accessing and modifying
     * the contents of the global clipboard.
     * 
     * @see #getSystemService
     * @see android.text.ClipboardManager
     */
    public static final String CLIPBOARD_SERVICE = "clipboard";

    /**
     * Determine whether the given permission is allowed for a particular
     * process and user ID running in the system.
     *
     * @param permission The name of the permission being checked.
     * @param pid The process ID being checked against.  Must be > 0.
     * @param uid The user ID being checked against.  A uid of 0 is the root
     * user, which will pass every permission check.
     *
     * @return Returns {@link PackageManager#PERMISSION_GRANTED} if the given
     * pid/uid is allowed that permission, or
     * {@link PackageManager#PERMISSION_DENIED} if it is not.
     *
     * @see PackageManager#checkPermission(String, String)
     * @see #checkCallingPermission
     */
    public abstract int checkPermission(String permission, int pid, int uid);

    /**
     * Determine whether the calling process of an IPC you are handling has been
     * granted a particular permission.  This is basically the same as calling
     * {@link #checkPermission(String, int, int)} with the pid and uid returned
     * by {@link android.os.Binder#getCallingPid} and
     * {@link android.os.Binder#getCallingUid}.  One important difference
     * is that if you are not currently processing an IPC, this function
     * will always fail.  This is done to protect against accidentally
     * leaking permissions; you can use {@link #checkCallingOrSelfPermission}
     * to avoid this protection.
     *
     * @param permission The name of the permission being checked.
     *
     * @return Returns {@link PackageManager#PERMISSION_GRANTED} if the calling
     * pid/uid is allowed that permission, or
     * {@link PackageManager#PERMISSION_DENIED} if it is not.
     *
     * @see PackageManager#checkPermission(String, String)
     * @see #checkPermission
     * @see #checkCallingOrSelfPermission
     */
    public abstract int checkCallingPermission(String permission);

    /**
     * Determine whether the calling process of an IPC <em>or you</em> have been
     * granted a particular permission.  This is the same as
     * {@link #checkCallingPermission}, except it grants your own permissions
     * if you are not currently processing an IPC.  Use with care!
     *
     * @param permission The name of the permission being checked.
     *
     * @return Returns {@link PackageManager#PERMISSION_GRANTED} if the calling
     * pid/uid is allowed that permission, or
     * {@link PackageManager#PERMISSION_DENIED} if it is not.
     *
     * @see PackageManager#checkPermission(String, String)
     * @see #checkPermission
     * @see #checkCallingPermission
     */
    public abstract int checkCallingOrSelfPermission(String permission);

    /**
     * If the given permission is not allowed for a particular process
     * and user ID running in the system, throw a {@link SecurityException}.
     *
     * @param permission The name of the permission being checked.
     * @param pid The process ID being checked against.  Must be &gt; 0.
     * @param uid The user ID being checked against.  A uid of 0 is the root
     * user, which will pass every permission check.
     * @param message A message to include in the exception if it is thrown.
     *
     * @see #checkPermission(String, int, int)
     */
    public abstract void enforcePermission(
            String permission, int pid, int uid, String message);

    /**
     * If the calling process of an IPC you are handling has not been
     * granted a particular permission, throw a {@link
     * SecurityException}.  This is basically the same as calling
     * {@link #enforcePermission(String, int, int, String)} with the
     * pid and uid returned by {@link android.os.Binder#getCallingPid}
     * and {@link android.os.Binder#getCallingUid}.  One important
     * difference is that if you are not currently processing an IPC,
     * this function will always throw the SecurityException.  This is
     * done to protect against accidentally leaking permissions; you
     * can use {@link #enforceCallingOrSelfPermission} to avoid this
     * protection.
     *
     * @param permission The name of the permission being checked.
     * @param message A message to include in the exception if it is thrown.
     *
     * @see #checkCallingPermission(String)
     */
    public abstract void enforceCallingPermission(
            String permission, String message);

    /**
     * If neither you nor the calling process of an IPC you are
     * handling has been granted a particular permission, throw a
     * {@link SecurityException}.  This is the same as {@link
     * #enforceCallingPermission}, except it grants your own
     * permissions if you are not currently processing an IPC.  Use
     * with care!
     *
     * @param permission The name of the permission being checked.
     * @param message A message to include in the exception if it is thrown.
     *
     * @see #checkCallingOrSelfPermission(String)
     */
    public abstract void enforceCallingOrSelfPermission(
            String permission, String message);



    /**
     * Flag for use with {@link #createPackageContext}: include the application
     * code with the context.  This means loading code into the caller's
     * process, so that {@link #getClassLoader()} can be used to instantiate
     * the application's classes.  Setting this flags imposes security
     * restrictions on what application context you can access; if the
     * requested application can not be safely loaded into your process,
     * java.lang.SecurityException will be thrown.  If this flag is not set,
     * there will be no restrictions on the packages that can be loaded,
     * but {@link #getClassLoader} will always return the default system
     * class loader.
     */
    public static final int CONTEXT_INCLUDE_CODE = 0x00000001;

    /**
     * Flag for use with {@link #createPackageContext}: ignore any security
     * restrictions on the Context being requested, allowing it to always
     * be loaded.  For use with {@link #CONTEXT_INCLUDE_CODE} to allow code
     * to be loaded into a process even when it isn't safe to do so.  Use
     * with extreme care!
     */
    public static final int CONTEXT_IGNORE_SECURITY = 0x00000002;


}
