package com.fishjord.irwidget;

import java.util.List;

import com.bfmj.network.INetworkService;
import com.bfmj.network.NetworkService;
import com.fishjord.irwidget.ir.codes.CodeManager;
import com.fishjord.irwidget.ir.codes.IRButton;
import com.fishjord.irwidget.ir.codes.IRCommand;
import com.fishjord.irwidget.ir.codes.Manufacturer;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;

public class ManufacturerActivity extends Activity {
	
	private CodeManager codeManager;
	//private IRService service;
	
	private INetworkService service;
	private Manufacturer manufacturer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.manufacturer);
	}
	@Override
	protected void onStart() {
		super.onStart();
		try {
			codeManager = CodeManager.getInstance(this.getApplicationContext());
			service=new NetworkService(this);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		List<String> manufacturers = codeManager.getManufacturers();
		manufacturer = CodeManager.getInstance().getManufacturer(
				manufacturers.get(0));

		Spinner spinner = (Spinner) findViewById(R.id.manufacturer);
		Log.d(this.getClass().getCanonicalName(), "Spinner: " + spinner);

		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, manufacturers);
		spinnerArrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
		spinner.setAdapter(spinnerArrayAdapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				updateSelectedManufacturer((String) parent
						.getItemAtPosition(pos));
				Log.i(this.getClass().getCanonicalName(), "Item selected: "
						+ manufacturer);
			}

			public void onNothingSelected(AdapterView<?> parent) {
				// manufacturer = (String)parent.getItemAtPosition(0);
				Log.i(this.getClass().getCanonicalName(), "No selected: "
						+ manufacturer);
			}

		});
	}

	public void updateSelectedManufacturer(String manny) {
		manufacturer = CodeManager.getInstance().getManufacturer(manny);
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.ButtonLayout);
		layout.removeAllViewsInLayout();

		int id = 0;
		int numButtons = 0;

		for (IRButton button : manufacturer.getButtons()) {
			final IRButton thisButton = button;
			// Button newButton = new Button(this);
			// hm, ignores the colors?
			Button newButton = new Button(new ContextThemeWrapper(
					ManufacturerActivity.this, R.style.btnStyleOrange));
			newButton.setText(thisButton.getDisplay());

			newButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					IRCommand irCommand = thisButton.getCommand();
					Log.d(this.getClass().getCanonicalName(),
							thisButton.getName() + " pushed, sending "
									+ irCommand);
					service.sendCommand(irCommand);
				}
			});

			RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);

			if (numButtons == 0) {
				relativeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				relativeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			} else {
				if (numButtons % 4 == 0) {
					relativeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
					relativeParams.addRule(RelativeLayout.BELOW, id);
				} else {
					relativeParams.addRule(RelativeLayout.RIGHT_OF, id);
					relativeParams.addRule(RelativeLayout.ALIGN_BOTTOM, id);
				}
			}
			numButtons++;
			id++;
			newButton.setId(id);

			// http://www.mindfreakerstuff.com/2012/09/50-useful-android-custom-button-style-set-1/
			/*
			 * int style = (Integer) null; if(numButtons % 2 == 0)
			 * style=R.style.btnStyleOrange; else
			 * style=R.style.btnStyleBlackpearl;
			 */
			newButton.setLayoutParams(relativeParams);

			layout.addView(newButton);
		}
	}
}