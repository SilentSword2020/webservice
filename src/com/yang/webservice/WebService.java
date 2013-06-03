package com.yang.webservice;

import java.io.IOException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class WebService extends Activity {

	private EditText phoneSecEditText;
	private TextView resultView;
	private Button queryButton;
	private Handler handler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_service);

		/*
		 * StrictMode.setThreadPolicy(new
		 * StrictMode.ThreadPolicy.Builder().detectDiskReads
		 * ().detectDiskWrites() .detectNetwork() // or .detectAll() for all
		 * detectable problems .penaltyLog().build());
		 * StrictMode.setVmPolicy(new
		 * StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects()
		 * .detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
		 */

		phoneSecEditText = (EditText) findViewById(R.id.phone_sec);
		resultView = (TextView) findViewById(R.id.result_text);
		queryButton = (Button) findViewById(R.id.query_btn);
		queryButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 手机号码（段）
				final String phoneSec = phoneSecEditText.getText().toString().trim();
				// 简单判断用户输入的手机号码（段）是否合法
				if ("".equals(phoneSec) || phoneSec.length() < 7) {
					// 给出错误提示
					phoneSecEditText.setError("您输入的手机号码（段）有误！");
					phoneSecEditText.requestFocus();
					// 将显示查询结果的TextView清空
					resultView.setText("");
					return;
				}
				// 查询手机号码（段）信息
				new Thread() {

					public void run() {
						getRemoteInfo(phoneSec);
					};
				}.start();

			}
		});
	}

	/**
	 * 手机号段归属地查询
	 * 
	 * @param phoneSec
	 *            手机号段
	 */
	public void getRemoteInfo(String phoneSec) {
		// 命名空间
		String nameSpace = "http://WebXml.com.cn/";
		// 调用的方法名称
		String methodName = "getMobileCodeInfo";
		// EndPoint
		String endPoint = "http://webservice.webxml.com.cn/WebServices/MobileCodeWS.asmx";
		// SOAP Action
		String soapAction = "http://WebXml.com.cn/getMobileCodeInfo";

		// 指定WebService的命名空间和调用的方法名
		SoapObject rpc = new SoapObject(nameSpace, methodName);

		// 设置需调用WebService接口需要传入的两个参数mobileCode、userId
		rpc.addProperty("mobileCode", phoneSec);
		rpc.addProperty("userID", "");

		// 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
		envelope.bodyOut = rpc;

		// 设置是否调用的是dotNet开发的WebService
		envelope.dotNet = true;

		// 等价于envelope.bodyOut = rpc;
		envelope.setOutputSoapObject(rpc);

		HttpTransportSE transport = new HttpTransportSE(endPoint);
		System.out.println("rpc:" + rpc);
		System.out.println("enevlope:" + envelope);

		// 调用WebService
		try {
			transport.call(soapAction, envelope);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (envelope.bodyIn == null) {
				getRemoteInfo(phoneSec);
			}
		} catch (XmlPullParserException e) {
			if (envelope.bodyIn == null) {
				getRemoteInfo(phoneSec);
			}
			e.printStackTrace();
		}

		// 获取返回的数据
		SoapObject object = (SoapObject) envelope.bodyIn;

		Log.d("SoapObject:", object + "");
		// 获取返回的结果
		final String result = object.getProperty(0).toString();
		handler.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				// 将WebService返回的结果显示在TextView中
				resultView.setText(result);

			}
		});

	}

}
