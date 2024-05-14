package com.example.d308vacationscheduler011542260.UI;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.d308vacationscheduler011542260.R;
import com.example.d308vacationscheduler011542260.database.Repository;
import com.example.d308vacationscheduler011542260.entities.Excursion;
import com.example.d308vacationscheduler011542260.entities.Vacation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class ExcursionDetails extends AppCompatActivity {

    String excursionTitle;
    int excursionID;
    int vacationID;
    String setDate;
    EditText editTitle;
    TextView editExcursionDate;
    DatePickerDialog.OnDateSetListener excursionDate;
    final Calendar myCalendar = Calendar.getInstance();
    String myFormat = "MM/dd/yy";
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
    Excursion currentExcursion;
    Repository repository;

    Random ran = new Random();
    int numAlert = ran.nextInt(9999);

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_excursion_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repository = new Repository(getApplication());
        editTitle = findViewById(R.id.excursionTitle);
        editExcursionDate = findViewById(R.id.excursionDate);
        excursionTitle = getIntent().getStringExtra("title");
        excursionID = getIntent().getIntExtra("id", -1);
        vacationID = getIntent().getIntExtra("vacationID", -1);
        setDate = getIntent().getStringExtra("excursionDate");
        editTitle.setText(excursionTitle);

        if (setDate != null)
            try {
                Date date = sdf.parse(setDate);
                myCalendar.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        excursionDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateLabel(editExcursionDate, myCalendar);
            }

        };

        editExcursionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date;
                String info = editExcursionDate.getText().toString();
                if (info.equals("")) info = setDate;
                try {
                    myCalendar.setTime(sdf.parse(info));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                new DatePickerDialog(ExcursionDetails.this, excursionDate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel(TextView edit, Calendar myCalendar) {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edit.setText(sdf.format(myCalendar.getTime()));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_excursiondetails, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateLabel(editExcursionDate, myCalendar);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        if (item.getItemId() == R.id.excursionsave) {
            if (!isValidDate(editExcursionDate.getText().toString())) {
                Toast.makeText(this, "Please enter valid date format (MM/dd/yy)", Toast.LENGTH_SHORT).show();
                return true;
            }

            Vacation vacation = null;
            for (Vacation vac : repository.getmAllVacations()) {
                if (vac.getVacationID() == vacationID)
                    vacation = vac;
            }

            if (vacationID == -1) {
                vacation = VacationDetails.tempVacation;
                vacationID = VacationDetails.tempVacationId;
            }

            String dateString = editExcursionDate.getText().toString();

            if (vacation != null)
                try {
                    Date date = sdf.parse(dateString);
                    Date vacStartDate = sdf.parse(vacation.getStartDate());
                    Date vacEndDate = sdf.parse(vacation.getEndDate());
                    if (date.before(vacStartDate) || date.after(vacEndDate)) {
                        Toast.makeText(this, "Excursion date must be between the vacation start and end dates", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            Excursion excursion;
            if (excursionID == -1) {
                if (repository.getmAllExcursions().size() == 0) excursionID = 1;
                else excursionID = repository.getmAllExcursions().get(repository.getmAllExcursions().size() - 1).getExcursionID() + 1;
                excursion = new Excursion(excursionID, editTitle.getText().toString(), vacationID, dateString);
                repository.insert(excursion);
                this.finish();
            } else {
                excursion = new Excursion(excursionID, editTitle.getText().toString(), vacationID, dateString);
                repository.update(excursion);
                this.finish();
            }

            return true;
        }

        if (item.getItemId() == R.id.excursiondelete) {
            for (Excursion excursion : repository.getmAllExcursions()) {
                if (excursion.getExcursionID() == excursionID) currentExcursion = excursion;
            }
            repository.delete(currentExcursion);
            Toast.makeText(ExcursionDetails.this, currentExcursion.getExcursionTitle() + " was deleted", Toast.LENGTH_LONG).show();
            ExcursionDetails.this.finish();
        }

        if (item.getItemId() == R.id.excursionnotify) {
            String startDateFromScreen = editExcursionDate.getText().toString();
            String startNotification = "Excursion: " + excursionTitle + " is today!";
            notifyHelper(startDateFromScreen, startNotification);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void notifyHelper(String dateFromScreen, String notification) {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        Date myDate = null;
        try {
            myDate = sdf.parse(dateFromScreen);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            Long trigger = myDate.getTime();
            Intent intent = new Intent(ExcursionDetails.this, MyReceiver.class);
            intent.putExtra("key", notification);
            PendingIntent sender = PendingIntent.getBroadcast(ExcursionDetails.this, numAlert, intent, PendingIntent.FLAG_IMMUTABLE);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, sender);
            numAlert = ran.nextInt(9999);
        } catch (Exception e) {

        }
    }

    private boolean isValidDate(String dateStr) {
        try {
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}