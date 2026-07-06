package com.krish.oibsip.reservation.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.krish.oibsip.reservation.service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private final AuthService authService;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkShowPassword;
    private JButton btnLogin;
    private JButton btnClear;
    private JLabel lblError;
    private JLabel lblLoading;

    public LoginFrame(AuthService authService) {
        this.authService = authService;
        
        setTitle("RailNexus - Sign In");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 640);
        setResizable(false);
        setLocationRelativeTo(null); // Center on screen
        
        initComponents();
        setupEvents();
    }

    private void initComponents() {
        // Main container panel with a distinct dark window background
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(UIConstants.COLOR_BG_WINDOW);
        
        // Centered Auth Card
        JPanel authCard = new JPanel(new BorderLayout(0, 20));
        authCard.setPreferredSize(new Dimension(380, 520));
        authCard.setBackground(UIConstants.COLOR_BG_CARD);
        authCard.putClientProperty(FlatClientProperties.STYLE, "arc: 16");
        authCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.COLOR_BORDER, 1),
                new EmptyBorder(30, 40, 30, 40)
        ));

        // 1. Brand Header Section
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.setOpaque(false);
        
        JLabel lblTitle = new JLabel("RAILNEXUS", JLabel.CENTER);
        lblTitle.setFont(new Font(UIConstants.FONT_FAMILY, Font.BOLD, 30));
        lblTitle.setForeground(UIConstants.COLOR_PRIMARY);
        
        JLabel lblSubtitle = new JLabel("Smart Railway Reservation System", JLabel.CENTER);
        lblSubtitle.setFont(UIConstants.FONT_MUTED);
        lblSubtitle.setForeground(Color.GRAY);
        
        headerPanel.add(lblTitle);
        headerPanel.add(lblSubtitle);
        authCard.add(headerPanel, BorderLayout.NORTH);

        // 2. Form fields panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0; // Ensure column stretches to fill parent container
        
        // Username Label & Textfield
        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(UIConstants.FONT_BODY_BOLD);
        lblUsername.setForeground(Color.LIGHT_GRAY);
        
        txtUsername = new JTextField(20);
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username");
        txtUsername.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        txtUsername.setPreferredSize(new Dimension(300, 40));
        txtUsername.setFont(UIConstants.FONT_BODY);
        txtUsername.setBackground(UIConstants.COLOR_INPUT_BG);
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.COLOR_BORDER, 1),
                new EmptyBorder(0, 10, 0, 10)
        ));

        // Password Label, Textfield & Show/Hide Checkbox
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(UIConstants.FONT_BODY_BOLD);
        lblPassword.setForeground(Color.LIGHT_GRAY);
        
        txtPassword = new JPasswordField(20);
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");
        txtPassword.setPreferredSize(new Dimension(300, 40));
        txtPassword.setFont(UIConstants.FONT_BODY);
        txtPassword.setBackground(UIConstants.COLOR_INPUT_BG);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.COLOR_BORDER, 1),
                new EmptyBorder(0, 10, 0, 10)
        ));

        chkShowPassword = new JCheckBox("Show Password");
        chkShowPassword.setFont(UIConstants.FONT_MUTED);
        chkShowPassword.setOpaque(false);
        chkShowPassword.setForeground(Color.LIGHT_GRAY);
        
        // Error Indicator
        lblError = new JLabel(" ", JLabel.CENTER);
        lblError.setFont(UIConstants.FONT_MUTED);
        lblError.setForeground(UIConstants.COLOR_DANGER);

        // Loading Indicator
        lblLoading = new JLabel(" ", JLabel.CENTER);
        lblLoading.setFont(new Font(UIConstants.FONT_FAMILY, Font.ITALIC, 11));
        lblLoading.setForeground(UIConstants.COLOR_PRIMARY);

        gbc.gridy = 0; formPanel.add(lblUsername, gbc);
        gbc.gridy = 1; formPanel.add(txtUsername, gbc);
        gbc.gridy = 2; formPanel.add(lblPassword, gbc);
        gbc.gridy = 3; formPanel.add(txtPassword, gbc);
        gbc.gridy = 4; formPanel.add(chkShowPassword, gbc);
        gbc.gridy = 5; formPanel.add(lblError, gbc);
        gbc.gridy = 6; formPanel.add(lblLoading, gbc);
        
        authCard.add(formPanel, BorderLayout.CENTER);

        // 3. Buttons & Demo card footer
        JPanel footerPanel = new JPanel(new BorderLayout(0, 15));
        footerPanel.setOpaque(false);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setOpaque(false);

        btnLogin = new JButton("Login");
        btnLogin.setFont(UIConstants.FONT_BODY_BOLD);
        btnLogin.setPreferredSize(new Dimension(140, 40));
        btnLogin.putClientProperty(FlatClientProperties.BUTTON_TYPE, "primary");
        btnLogin.setBackground(UIConstants.COLOR_PRIMARY);
        btnLogin.setForeground(Color.WHITE);

        btnClear = new JButton("Clear");
        btnClear.setFont(UIConstants.FONT_BODY_BOLD);
        btnClear.setPreferredSize(new Dimension(140, 40));

        buttonPanel.add(btnClear);
        buttonPanel.add(btnLogin);
        footerPanel.add(buttonPanel, BorderLayout.NORTH);

        // Demo Account compact card with deep background contrast
        JPanel demoPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        demoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(UIConstants.COLOR_BORDER, 1), "Demo Evaluation Accounts"),
                new EmptyBorder(8, 12, 8, 12)
        ));
        demoPanel.setBackground(UIConstants.COLOR_BG_WINDOW);
        demoPanel.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        
        JLabel lblHint1 = new JLabel("Account 1:  krish  /  demo123");
        JLabel lblHint2 = new JLabel("Account 2:  passenger1  /  pass123");
        lblHint1.setFont(UIConstants.FONT_MUTED);
        lblHint2.setFont(UIConstants.FONT_MUTED);
        lblHint1.setForeground(Color.GRAY);
        lblHint2.setForeground(Color.GRAY);
        demoPanel.add(lblHint1);
        demoPanel.add(lblHint2);
        
        footerPanel.add(demoPanel, BorderLayout.SOUTH);
        authCard.add(footerPanel, BorderLayout.SOUTH);

        mainPanel.add(authCard);
        setContentPane(mainPanel);
        getRootPane().setDefaultButton(btnLogin);
    }

    private void setupEvents() {
        chkShowPassword.addActionListener(e -> {
            if (chkShowPassword.isSelected()) {
                txtPassword.setEchoChar((char) 0);
            } else {
                txtPassword.setEchoChar('•');
            }
        });

        btnClear.addActionListener(e -> {
            txtUsername.setText("");
            txtPassword.setText("");
            lblError.setText(" ");
        });

        btnLogin.addActionListener(e -> handleLogin());

        txtUsername.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtPassword.requestFocusInWindow();
                }
            }
        });
    }

    private void handleLogin() {
        String username = txtUsername.getText();
        String password = new String(txtPassword.getPassword());
        lblError.setText(" ");
        
        if (username.trim().isEmpty()) {
            lblError.setText("Username is required.");
            txtUsername.requestFocus();
            return;
        }
        if (password.trim().isEmpty()) {
            lblError.setText("Password is required.");
            txtPassword.requestFocus();
            return;
        }

        setLoading(true);

        SwingWorker<Boolean, Void> loginWorker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                Thread.sleep(300);
                return authService.login(username, password);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    setLoading(false);
                    if (success) {
                        new MainFrame(authService).setVisible(true);
                        dispose();
                    } else {
                        lblError.setText("Invalid username or password.");
                        txtPassword.setText("");
                        txtPassword.requestFocus();
                    }
                } catch (Exception ex) {
                    setLoading(false);
                    txtPassword.setText("");
                    Throwable cause = ex.getCause();
                    if (cause instanceof SQLException) {
                        lblError.setText("Database Connection Error.");
                    } else {
                        lblError.setText("An unexpected error occurred.");
                    }
                }
            }
        };
        loginWorker.execute();
    }

    private void setLoading(boolean loading) {
        if (loading) {
            btnLogin.setEnabled(false);
            btnClear.setEnabled(false);
            txtUsername.setEnabled(false);
            txtPassword.setEnabled(false);
            chkShowPassword.setEnabled(false);
            lblLoading.setText("Authenticating, please wait...");
        } else {
            btnLogin.setEnabled(true);
            btnClear.setEnabled(true);
            txtUsername.setEnabled(true);
            txtPassword.setEnabled(true);
            chkShowPassword.setEnabled(true);
            lblLoading.setText(" ");
        }
    }
}
