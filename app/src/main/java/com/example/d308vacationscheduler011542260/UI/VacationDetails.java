package com.example.d308vacationscheduler011542260.UI;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308vacationscheduler011542260.R;
import com.example.d308vacationscheduler011542260.database.Repository;
import com.example.d308vacationscheduler011542260.entities.Excursion;
import com.example.d308vacationscheduler011542260.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class VacationDetails extends AppCompatActivity {

    String title;
    String hotelName;
    int vacationID;
    public static int tempVacationId;
    public static Vacation tempVacation;
    String setStartDate;
    String setEndDate;

    EditText editTitle;
    EditText editHotelName;
    TextView editStartDate;
    TextView editEndDate;
    Vacation currentVacation;
    int numExcursions;
    DatePickerDialog.OnDateSetListener startDate;
    DatePickerDialog.OnDateSetListener endDate;
    final Calendar myCalendarStart = Calendar.getInstance();
    final Calendar myCalendarEnd = Calendar.getInstance();
    String myFormat = "MM/dd/yy";
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

    Repository repository;

    List<Excursion> filteredExcursions = new ArrayList<>();

    Random ran = new Random();
    int numAlert = ran.nextInt(9999);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTitle = findViewById(R.id.vacationTitle);
        editHotelName = findViewById(R.id.hotelName);
        title = getIntent().getStringExtra("title");
        hotelName = getIntent().getStringExtra("hotel name");
        vacationID = getIntent().getIntExtra("id", -1);
        setStartDate = getIntent().getStringExtra("start date");
        setEndDate = getIntent().getStringExtra("end date");
        editTitle.setText(title);
        editHotelName.setText(hotelName);
        editStartDate = findViewById(R.id.startdate);
        editEndDate = findViewById(R.id.enddate);

        FloatingActionButton fab = findViewById(R.id.floatingActionButton2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Vacation vac : repository.getmAllVacations()) {
                    if (vac.getVacationID() == vacationID) currentVacation = vac;
                }
                tempVacation = currentVacation;
                tempVacationId = vacationID;
                Intent intent = new Intent(VacationDetails.this, ExcursionDetails.class);
                startActivity(intent);
            }
        });

        repository = new Repository(getApplication());

        RecyclerView recyclerView = findViewById(R.id.excursionrecyclerview);
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        for (Excursion e : repository.getmAllExcursions()) {
            if (e.getVacationID() == vacationID) filteredExcursions.add(e);
        }

        excursionAdapter.setmExcursions(filteredExcursions);

        if (setStartDate != null)
            try {
                Date startDate = sdf.parse(setStartDate);
                Date endDate = sdf.parse(setEndDate);
                myCalendarStart.setTime(startDate);
                myCalendarEnd.setTime(endDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        startDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendarStart.set(Calendar.YEAR, year);
                myCalendarStart.set(Calendar.MONTH, monthOfYear);
                myCalendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateLabel(editStartDate, myCalendarStart);
            }

        };

        editStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date;
                String info = editStartDate.getText().toString();
                if (info.equals("")) info = setStartDate;
                try {
                    myCalendarStart.setTime(sdf.parse(info));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                new DatePickerDialog(VacationDetails.this, startDate, myCalendarStart
                        .get(Calendar.YEAR), myCalendarStart.get(Calendar.MONTH),
                        myCalendarStart.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        endDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendarEnd.set(Calendar.YEAR, year);
                myCalendarEnd.set(Calendar.MONTH, monthOfYear);
                myCalendarEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateLabel(editEndDate, myCalendarEnd);
            }

        };

        editEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date;
                String info = editEndDate.getText().toString();
                if (info.equals("")) info = setEndDate;
                try {
                    myCalendarEnd.setTime(sdf.parse(info));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                new DatePickerDialog(VacationDetails.this, endDate, myCalendarEnd
                        .get(Calendar.YEAR), myCalendarEnd.get(Calendar.MONTH),
                        myCalendarEnd.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateLabel(TextView edit, Calendar myCalendar) {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edit.setText(sdf.format(myCalendar.getTime()));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacationdetails, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        RecyclerView recyclerView = findViewById(R.id.excursionrecyclerview);
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        filteredExcursions.clear();

        for (Excursion e : repository.getmAllExcursions()) {
            if (e.getVacationID() == vacationID) filteredExcursions.add(e);
        }

        excursionAdapter.setmExcursions(filteredExcursions);

        updateLabel(editStartDate, myCalendarStart);
        updateLabel(editEndDate, myCalendarEnd);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        if (item.getItemId() == R.id.vacationsave) {
            if (!isValidDate(editStartDate.getText().toString()) || !isValidDate(editEndDate.getText().toString())) {
                Toast.makeText(this, "Please enter valid date format (MM/dd/yy)", Toast.LENGTH_SHORT).show();
                return true;
            }

            try {
                Date startDate = sdf.parse(editStartDate.getText().toString());
                Date endDate = sdf.parse(editEndDate.getText().toString());
                if (startDate.after(endDate)) {
                    Toast.makeText(this, "End date must be after the start date", Toast.LENGTH_SHORT).show();
                    return true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String startDateString = editStartDate.getText().toString();
            String endDateString = editEndDate.getText().toString();

            Vacation vacation;
            if (vacationID == -1) {
                if (repository.getmAllVacations().size() == 0) vacationID = 1;
                else vacationID = repository.getmAllVacations().get(repository.getmAllVacations().size() - 1).getVacationID() + 1;
                vacation = new Vacation(vacationID, editTitle.getText().toString(), editHotelName.getText().toString(), startDateString, endDateString);
                repository.insert(vacation);
                this.finish();
            }
            else {
                vacation = new Vacation(vacationID, editTitle.getText().toString(), editHotelName.getText().toString(), startDateString, endDateString);
                repository.update(vacation);
                this.finish();
            }
        }

        if (item.getItemId() == R.id.vacationdelete) {
            for (Vacation vac : repository.getmAllVacations()) {
                if (vac.getVacationID() == vacationID) currentVacation = vac;
            }
            numExcursions = 0;
            for (Excursion excursion : repository.getmAllExcursions()) {
                if (excursion.getVacationID() == vacationID) ++numExcursions;
            }

            if (numExcursions == 0) {
                repository.delete(currentVacation);
                Toast.makeText(VacationDetails.this, currentVacation.getVacationTitle() + " was deleted", Toast.LENGTH_LONG).show();
                VacationDetails.this.finish();
            } else {
                Toast.makeText(VacationDetails.this, "Can't delete a vacation with excursions", Toast.LENGTH_LONG).show();
            }
        }

        if (item.getItemId() == R.id.vacationshare) {
            Intent sentIntent = new Intent();
            sentIntent.setAction(Intent.ACTION_SEND);
            sentIntent.putExtra(Intent.EXTRA_TITLE, "Shared Vacation");

            StringBuilder sharedString = new StringBuilder();
            sharedString.append("Vacation Title: " + editTitle.getText().toString() + "\n");
            sharedString.append("Hotel Name: " + editHotelName.getText().toString() + "\n");
            sharedString.append("Start Date: " + editStartDate.getText().toString() + "\n");
            sharedString.append("End Date: " + editEndDate.getText().toString() + "\n");
            if (!filteredExcursions.isEmpty()) {
                sharedString.append("Excursions: \n");
                for (int i = 0; i < filteredExcursions.size(); i++) {
                    sharedString.append(filteredExcursions.get(i).getExcursionDate() + ": " + filteredExcursions.get(i).getExcursionTitle() + "\n");
                }
            } else {
                sharedString.append("This vacation has no excursions.");
            }
            sentIntent.putExtra(Intent.EXTRA_TEXT, sharedString.toString());
            sentIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sentIntent, null);
            startActivity(shareIntent);
            return true;
        }

        if (item.getItemId() == R.id.vacationnotify) {
            String startDateFromScreen = editStartDate.getText().toString();
            String startNotification = "Vacation " + title + " is starting";
            notifyHelper(startDateFromScreen, startNotification);
            String endDateFromScreen = editEndDate.getText().toString();
            String endNotification = "Vacation " + title + " is ending";
            notifyHelper(endDateFromScreen, endNotification);

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
            Intent intent = new Intent(VacationDetails.this, MyReceiver.class);
            intent.putExtra("key", notification);
            PendingIntent sender = PendingIntent.getBroadcast(VacationDetails.this, numAlert, intent, PendingIntent.FLAG_IMMUTABLE);
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