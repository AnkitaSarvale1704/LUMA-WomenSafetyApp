package com.example.allianzproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class EmergencyContactsActivity extends AppCompatActivity {

    LinearLayout contactsContainer, addForm;
    EditText etName, etPhone;
    Button btnSave, btnAdd;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);


        contactsContainer = findViewById(R.id.contactsContainer);
        addForm = findViewById(R.id.addForm);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        btnSave = findViewById(R.id.btnSave);
        btnAdd = findViewById(R.id.btnAdd);

        prefs = getSharedPreferences("LUMA_PREFS", MODE_PRIVATE);

        btnAdd.setOnClickListener(v -> addForm.setVisibility(View.VISIBLE));
        btnSave.setOnClickListener(v -> saveContact());

        loadContacts();
    }

    private void saveContact() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (phone.isEmpty()) {
            Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty()) name = "Emergency Contact";

        // 🔥 Use StringBuilder to avoid overwriting
        String existing = prefs.getString("CONTACTS", "");
        String newData;

        if (existing == null || existing.trim().isEmpty()) {
            newData = name + "|" + phone + ";";
        } else {
            newData = existing + name + "|" + phone + ";";
        }

        prefs.edit().putString("CONTACTS", newData).apply();

        addForm.setVisibility(View.GONE);
        etName.setText("");
        etPhone.setText("");
        loadContacts();
    }

    private void loadContacts() {
        contactsContainer.removeAllViews();

        String data = prefs.getString("CONTACTS", null);
        if (data == null || data.trim().isEmpty()) return;

        for (String item : data.split(";")) {
            if (item.trim().isEmpty()) continue;

            String[] parts = item.split("\\|");
            if (parts.length < 2) continue;

            String name = parts[0];
            String phone = parts[1];

            View card = getLayoutInflater().inflate(R.layout.contact_item, contactsContainer, false);

            TextView txtName = card.findViewById(R.id.txtName);
            TextView txtPhone = card.findViewById(R.id.txtPhone);
            ImageView btnDelete = card.findViewById(R.id.btnDelete);

            txtName.setText(name);
            txtPhone.setText(phone);

            // 🔥 Identify this exact contact entry
            String contactEntry = name + "|" + phone;

            // 🗑 DELETE LOGIC
            btnDelete.setOnClickListener(v -> {
                String all = prefs.getString("CONTACTS", "");
                StringBuilder newData = new StringBuilder();

                for (String item2 : all.split(";")) {
                    if (item2.trim().isEmpty()) continue;

                    if (!item2.equals(contactEntry)) {
                        newData.append(item2).append(";");
                    }
                }

                prefs.edit().putString("CONTACTS", newData.toString()).apply();
                loadContacts(); // refresh list
            });

            contactsContainer.addView(card);
        }
    }
}