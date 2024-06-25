package com.example.a175.Activity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.a175.Adapter.CartAdapter;
import com.example.a175.Domain.ItemsDomain;
import com.example.a175.Helper.ChangeNumberItemsListener;
import com.example.a175.Helper.ManagmentCart;
import com.example.a175.Helper.Order;
import com.example.a175.R;
import com.example.a175.databinding.ActivityCartBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CartActivity extends BaseActivity {
    ActivityCartBinding binding;
    private double tax;
    private ManagmentCart managmentCart;
    private DatabaseReference databaseReference;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managmentCart = new ManagmentCart(this);

        // Lấy tên người dùng từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "User");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Orders").child(username); // Lưu đơn hàng dưới tên người dùng

        caculatorCart();
        setVariable();
        initCartList();
        setCheckoutButtonListener();
    }

    private void initCartList() {
        if (managmentCart.getListCart().isEmpty()) {
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scrollViewCart.setVisibility(View.GONE);
        } else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scrollViewCart.setVisibility(View.VISIBLE);
        }

        binding.cartView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.cartView.setAdapter(new CartAdapter(managmentCart.getListCart(), this, new ChangeNumberItemsListener() {
            @Override
            public void changed() {
                caculatorCart();
            }
        }));
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void caculatorCart() {
        double percentTax = 0.02;
        double delivery = 10;
        tax = Math.round(managmentCart.getTotalFee() + percentTax * 100.0) / 100.0;
        double total = Math.round((managmentCart.getTotalFee() + tax + delivery) * 100.0) / 100.0;
        double itemTotal = Math.round(managmentCart.getTotalFee() * 100.0) / 100.0;

        binding.totalFeeTxt.setText("$" + itemTotal);
        binding.taxTxt.setText("$" + tax);
        binding.deliveryTxt.setText("$" + delivery);
        binding.totalTxt.setText("$" + total);
    }

    private void setCheckoutButtonListener() {
        binding.checkOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hiển thị dialog để nhập thông tin địa chỉ
                showAddressDialog();
            }
        });
    }

    private void showAddressDialog() {
        // Tạo Dialog
        Dialog dialog = new Dialog(CartActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_address, null);
        dialog.setContentView(dialogView);

        // Thiết lập kích thước và vị trí cho Dialog
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        EditText nameEditText = dialogView.findViewById(R.id.nameEditText);
        EditText phoneEditText = dialogView.findViewById(R.id.phoneEditText);
        EditText addressEditText = dialogView.findViewById(R.id.addressEditText);
        RadioGroup paymentMethodGroup = dialogView.findViewById(R.id.paymentMethodGroup);
        Button confirmButton = dialogView.findViewById(R.id.confirmButton);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();
                String address = addressEditText.getText().toString().trim();
                int selectedPaymentMethodId = paymentMethodGroup.getCheckedRadioButtonId();
                String paymentMethod = "Tiền mặt";

                if (selectedPaymentMethodId != -1) {
                    RadioButton selectedPaymentMethod = dialogView.findViewById(selectedPaymentMethodId);
                    paymentMethod = selectedPaymentMethod.getText().toString();
                }

                if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                    Toast.makeText(CartActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else {
                    // Lấy danh sách các mặt hàng trong giỏ hàng
                    ArrayList<ItemsDomain> cartItems = managmentCart.getListCart();

                    // Tạo đơn hàng mới
                    Order order = new Order();
                    order.setItems(cartItems);
                    order.setTotalPrice(Double.parseDouble(binding.totalTxt.getText().toString().replace("$", "")));
                    order.setTax(Double.parseDouble(binding.taxTxt.getText().toString().replace("$", "")));
                    order.setDeliveryFee(10.0);
                    order.setName(name);
                    order.setPhone(phone);
                    order.setAddress(address);
                    order.setPaymentMethod(paymentMethod);

                    // Lưu đơn hàng vào Firebase dưới tên người dùng
                    databaseReference.push().setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(CartActivity.this, "Đã lưu đơn hàng thành công", Toast.LENGTH_SHORT).show();
                                // Xóa giỏ hàng sau khi thanh toán thành công
                                managmentCart.clearCart();
                                dialog.dismiss();
                                finish();
                            } else {
                                Toast.makeText(CartActivity.this, "Lỗi khi lưu đơn hàng", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        dialog.show();
    }

}
