package com.yzrilyzr.FAQ;

import android.widget.*;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import com.yzrilyzr.FAQ.Data.MessageObj;
import com.yzrilyzr.FAQ.Data.ToStrObj;
import com.yzrilyzr.FAQ.Data.User;
import com.yzrilyzr.FAQ.Main.C;
import com.yzrilyzr.FAQ.Main.ClientService;
import com.yzrilyzr.FAQ.Main.Data;
import com.yzrilyzr.ui.myRoundDrawable;
import com.yzrilyzr.ui.myToolBar;

public class MainActivity extends BaseActivity
{
	User to;
	EditText et;
	LinearLayout ll;
	Button bt;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		AndroidBug5497Workaround.assistActivity(this);
		to=(User) ToStrObj.s2o(getIntent().getStringExtra("user"));
		setTitle(to.nick);
		ll=(LinearLayout) findViewById(R.id.mainLinearLayout1);
		et=(EditText) findViewById(R.id.mainEditText1);
		bt=(Button) findViewById(R.id.mainButton1);
		bt.setEnabled(false);
		myToolBar mt=(myToolBar) findViewById(R.id.mainmyToolBar1);
		mt.setTitle(to.nick);
		et.setOnFocusChangeListener(new OnFocusChangeListener(){
				@Override
				public void onFocusChange(View p1, boolean p2)
				{
					// TODO: Implement this method
					new Handler().postDelayed(new Runnable(){
							@Override
							public void run()
							{
								// TODO: Implement this method
								((ScrollView)ll.getParent()).fullScroll(View.FOCUS_DOWN);
							}
						},300);
				}
			});
		et.addTextChangedListener(new TextWatcher(){
				@Override
				public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					// TODO: Implement this method
				}

				@Override
				public void onTextChanged(CharSequence p1, int p2, int p3, int p4)
				{
					// TODO: Implement this method
					if(p1.length()==0)bt.setEnabled(false);
					else bt.setEnabled(true);
				}

				@Override
				public void afterTextChanged(Editable p1)
				{
					// TODO: Implement this method
				}
			});
    }

	@Override
	public void rev(byte cmd, final String msg)
	{
		// TODO: Implement this method
		if(cmd==C.MSG)
		{
			MessageObj m=(MessageObj)ToStrObj.s2o(msg);
			if(m.from==to.faq)
				runOnUiThread(new Runnable(){
						@Override
						public void run()
						{
							// TODO: Implement this method
							MessageObj m=(MessageObj) ToStrObj.s2o(msg);
							byte[] mh=Data.getHead(m.from,false);
							addMsg(to.nick,false,false,BitmapFactory.decodeByteArray(mh,0,mh.length),m.msg);
						}
					});
		}
	}
	public void send(View v)
	{
		String m=et.getText().toString();
		et.setText("");
		byte[] mh=Data.getMyHead();
		User u=Data.getMyself();
		addMsg(u.nick,true,false,BitmapFactory.decodeByteArray(mh,0,mh.length),m);
		if(!ClientService.sendMsg(C.MSG,new MessageObj(u.faq,to.faq,(byte)0,false,m).setTime().o2s()))Toast.makeText(this,"????????????",0).show();
	}

	@Override
	public void exit(View v)
	{
		// TODO: Implement this method
		et.setEnabled(false);
		super.exit(v);
	}

	private void addMsg(String fromNick,boolean isSend,boolean isGroup,Bitmap head,String msg)
	{
		ViewGroup vg=(ViewGroup) LayoutInflater.from(this).inflate(R.layout.msg_entry,null);
		((LinearLayout)vg.findViewById(R.id.msgentryLinearLayout1)).setGravity(isSend?Gravity.RIGHT:Gravity.LEFT);
		TextView ni=(TextView) vg.findViewById(R.id.msgentryTextView1);
		ni.setVisibility(isGroup?0:8);
		if(fromNick!=null)ni.setText(fromNick);
		TextView tv=(TextView) vg.findViewById(R.id.msgentryTextView2);
		tv.setText(msg);
		Bitmap b=BitmapFactory.decodeResource(getResources(),R.drawable.aio_user_bg_nor);
		//Matrix mat=new Matrix();
		//mat.postScale(isSend?1f:-1f,1f);
		//b=Bitmap.createBitmap(b,0,0,b.getWidth(),b.getHeight(),mat,false);
		byte[] nc=b.getNinePatchChunk();
		if(NinePatch.isNinePatchChunk(nc))
		{
			NinePatchDrawable n=new NinePatchDrawable(getResources(),b,nc,
													  new Rect(tv.getPaddingLeft(),tv.getPaddingTop(),tv.getPaddingRight(),tv.getPaddingBottom())
													  ,null);
			//n.setAutoMirrored(!isSend);
			tv.setBackground(n);
		}
		myRoundDrawable de=new myRoundDrawable(head);
		ImageView iv1=(ImageView) vg.findViewById(R.id.msgentryImageView1);
		ImageView iv2=(ImageView) vg.findViewById(R.id.msgentryImageView2);
		if(isSend)iv2.setImageDrawable(de);
		else iv1.setImageDrawable(de);
		ll.addView(vg);
		new Handler().postDelayed(new Runnable(){
				@Override
				public void run()
				{
					// TODO: Implement this method
					((ScrollView)ll.getParent()).fullScroll(View.FOCUS_DOWN);
				}
			},300);
	}

}
