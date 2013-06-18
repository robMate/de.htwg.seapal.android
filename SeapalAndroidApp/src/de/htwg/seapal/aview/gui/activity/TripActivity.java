package de.htwg.seapal.aview.gui.activity;

import java.util.List;
import java.util.UUID;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.inject.Inject;

import de.htwg.seapal.R;
import de.htwg.seapal.controller.impl.TripController;
import de.htwg.seapal.utils.observer.Event;
import de.htwg.seapal.utils.observer.IObserver;

public class TripActivity extends BaseDrawerActivity implements IObserver {

	@Inject
	private TripController controller;
	private UUID trip;

	private EditText triptitle;
	private EditText from;
	private EditText to;
	private EditText start;
	private EditText end;
	private EditText skipper;
	private EditText crew;
	private EditText duration;
	private EditText notes;
	private EditText engine;
	private EditText tank;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.trip);
		Bundle extras = getIntent().getExtras();
		trip = UUID.fromString(extras.getString("trip"));

		triptitle = (EditText) findViewById(R.id.trip_editTripname);
		from = (EditText) findViewById(R.id.trip_editFrom);
		to = (EditText) findViewById(R.id.trip_editTo);
		start = (EditText) findViewById(R.id.trip_editStart);
		start.setFocusable(false);
		end = (EditText) findViewById(R.id.trip_editEnd);
		end.setFocusable(false);
		skipper = (EditText) findViewById(R.id.trip_editSkipper);
		crew = (EditText) findViewById(R.id.trip_editCrew);
		duration = (EditText) findViewById(R.id.trip_editDuration);
		duration.setFocusable(false);
		notes = (EditText) findViewById(R.id.trip_editNotes);
		engine = (EditText) findViewById(R.id.trip_editEngine);
		engine.setInputType(InputType.TYPE_CLASS_NUMBER);
		tank = (EditText) findViewById(R.id.trip_editTank);
		tank.setInputType(InputType.TYPE_CLASS_NUMBER
				| InputType.TYPE_NUMBER_FLAG_DECIMAL);
		fillText();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.tripmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (!controller.getName(trip).equals(triptitle.getText().toString()))
			controller.setName(trip, triptitle.getText().toString());
		if (!controller.getStartLocation(trip)
				.equals(from.getText().toString()))
			controller.setStartLocation(trip, from.getText().toString());
		if (!controller.getEndLocation(trip).equals(to.getText().toString()))
			controller.setEndLocation(trip, to.getText().toString());
		if (controller.getMotor(trip) != Integer.valueOf(engine.getText()
				.toString()))
			controller.setMotor(trip,
					Integer.valueOf(engine.getText().toString()));
		if (controller.getFuel(trip) != Double.valueOf(tank.getText()
				.toString()))
			controller.setFuel(trip, Double.valueOf(tank.getText().toString()));

		// skipper

		if (!controller.getNotes(trip).equals(notes.getText().toString()))
			controller.setNotes(trip, notes.getText().toString());

		controller.addCrewMember(trip, crew.getText().toString());

		Toast.makeText(this, "Saved Changes", Toast.LENGTH_SHORT).show();

		return true;
	}

	@Override
	public void update(Event event) {
		fillText();
	}

	private void fillText() {

		triptitle.setText(controller.getName(trip));
		from.setText(controller.getStartLocation(trip));
		to.setText(controller.getEndLocation(trip));
		start.setText(DateFormat.format("yyyy/MM/dd hh:mm",
				controller.getStartTime(trip)));
		end.setText(DateFormat.format("yyyy/MM/dd hh:mm",
				controller.getEndTime(trip)));

		if (controller.getSkipper(trip) == null)
			skipper.setText("-");
		else
			skipper.setText(controller.getSkipper(trip).toString());

		duration.setText(calcDuration());
		notes.setText(controller.getNotes(trip));
		engine.setText(Integer.toString(controller.getMotor(trip)));
		tank.setText(Double.toString(controller.getFuel(trip)));

		List<String> crewMembers = controller.getCrewMembers(trip);
		try {
			crew.setText(crewMembers.get(0));
		} catch (IndexOutOfBoundsException e) {
			crew.setText("-");
		}

	}

	private String calcDuration() {
		long l1 = controller.getStartTime(trip);
		long l2 = controller.getEndTime(trip);
		long diff = l2 - l1;

		long secondInMillis = 1000;
		long minuteInMillis = secondInMillis * 60;
		long hourInMillis = minuteInMillis * 60;
		long dayInMillis = hourInMillis * 24;

		long elapsedDays = diff / dayInMillis;
		diff = diff % dayInMillis;
		long elapsedHours = diff / hourInMillis;
		diff = diff % hourInMillis;
		long elapsedMinutes = diff / minuteInMillis;
		diff = diff % minuteInMillis;
		long elapsedSeconds = diff / secondInMillis;

		return elapsedDays + "d " + elapsedHours + "h " + elapsedMinutes + "m "
				+ elapsedSeconds + "s";
	}

}