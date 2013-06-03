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
				// �ֻ����루�Σ�
				final String phoneSec = phoneSecEditText.getText().toString().trim();
				// ���ж��û�������ֻ����루�Σ��Ƿ�Ϸ�
				if ("".equals(phoneSec) || phoneSec.length() < 7) {
					// ����������ʾ
					phoneSecEditText.setError("��������ֻ����루�Σ�����");
					phoneSecEditText.requestFocus();
					// ����ʾ��ѯ�����TextView���
					resultView.setText("");
					return;
				}
				// ��ѯ�ֻ����루�Σ���Ϣ
				new Thread() {

					public void run() {
						getRemoteInfo(phoneSec);
					};
				}.start();

			}
		});
	}

	/**
	 * �ֻ��Ŷι����ز�ѯ
	 * 
	 * @param phoneSec
	 *            �ֻ��Ŷ�
	 */
	public void getRemoteInfo(String phoneSec) {
		// �����ռ�
		String nameSpace = "http://WebXml.com.cn/";
		// ���õķ�������
		String methodName = "getMobileCodeInfo";
		// EndPoint
		String endPoint = "http://webservice.webxml.com.cn/WebServices/MobileCodeWS.asmx";
		// SOAP Action
		String soapAction = "http://WebXml.com.cn/getMobileCodeInfo";

		// ָ��WebService�������ռ�͵��õķ�����
		SoapObject rpc = new SoapObject(nameSpace, methodName);

		// ���������WebService�ӿ���Ҫ�������������mobileCode��userId
		rpc.addProperty("mobileCode", phoneSec);
		rpc.addProperty("userID", "");

		// ���ɵ���WebService������SOAP������Ϣ,��ָ��SOAP�İ汾
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
		envelope.bodyOut = rpc;

		// �����Ƿ���õ���dotNet������WebService
		envelope.dotNet = true;

		// �ȼ���envelope.bodyOut = rpc;
		envelope.setOutputSoapObject(rpc);

		HttpTransportSE transport = new HttpTransportSE(endPoint);
		System.out.println("rpc:" + rpc);
		System.out.println("enevlope:" + envelope);

		// ����WebService
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

		// ��ȡ���ص�����
		SoapObject object = (SoapObject) envelope.bodyIn;

		Log.d("SoapObject:", object + "");
		// ��ȡ���صĽ��
		final String result = object.getProperty(0).toString();
		handler.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				// ��WebService���صĽ����ʾ��TextView��
				resultView.setText(result);

			}
		});

	}

}
