package com.mycompany.hospitalmetropolitanocrud;

import com.mycompany.hospitalmetropolitanocrud.vista.VentanaHospital;
import javax.swing.SwingUtilities;

public class HospitalMetropolitanoCrud {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VentanaHospital().setVisible(true);
        });
    }
}