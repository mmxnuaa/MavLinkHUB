package com.paku.mavlinkhub;

import com.paku.mavlinkhub.enums.UI_MODE;
import com.paku.mavlinkhub.fragments.FragmentsAdapter;
import com.paku.mavlinkhub.messenger.HUBMessenger;
import com.paku.mavlinkhub.queue.endpoints.DroneClient;
import com.paku.mavlinkhub.queue.endpoints.GroundStationServer;
import com.paku.mavlinkhub.queue.endpoints.drone.DroneClientBluetooth;
import com.paku.mavlinkhub.queue.endpoints.gs.GroundStationServerTCP;
import com.paku.mavlinkhub.queue.hubqueue.HUBQueue;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;

public class HUBGlobals extends Application {

	@SuppressWarnings("unused")
	private static final String TAG = "HUBGlobals";

	// constants
	// buffer, stream sizes
	public int visibleBuffSize = 256 * 8;
	// public int minStreamReadSize = 2 ^ 4; // ^6 = 64 ^5=32 ^4=16
	public int visibleMsgList = 50;
	public int serverTCP_port = 5760;

	// colors

	public String colDark = "#008893";
	public String colDarkMedium = "#7BC1C7";
	public String colMedium = "#4AAAb2";
	public String colLightMedium = "#16929C";
	public String colLight = "#A5D5D8";

	// messages handler
	public HUBMessenger messenger;

	// main Drone connector
	public DroneClient droneClient;

	// main GS connector
	public GroundStationServer gsServer;

	// main ItemMavLinkMsg objects queue
	public HUBQueue queue;

	// sys log stats holder object
	public HUBLogger logger;

	// we are a Fragment Application
	public FragmentsAdapter mFragmentsPagerAdapter;
	public ViewPager mViewPager;

	public SharedPreferences prefs;

	// with initial state as "created"
	public UI_MODE uiMode;

	public void hubInit(Context mContext) {

		uiMode = UI_MODE.UI_MODE_CREATED;

		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		// start application asynchronous messaging - has to be first !!!
		messenger = new HUBMessenger(this);

		logger = new HUBLogger(this);

		droneClient = new DroneClientBluetooth(messenger.appMsgHandler);

		// server started from the beginning
		gsServer = new GroundStationServerTCP(messenger.appMsgHandler);

		gsServer.startServer(serverTCP_port);

		// finally start parsers and distributors
		queue = new HUBQueue(this, 1000);
		queue.startQueue();

	}

}
