/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package inventariobodega;

import javax.swing.JOptionPane;

/**
 *
 * @author DJL
 */
public class LE {
/**
 * Muestra un cuadro de diálogo solicitando el ingreso de un caracter.
 * @param mensaje especifica la solicitud del caracter a ingresar.
 */
public static char leerChar(String mensaje) {
	String cadena;
	do{
		cadena=JOptionPane.showInputDialog(mensaje);
		if(cadena==null)
			mostrarError("Error... no se ha ingresado valor");
		if(cadena.length()>1)
			mostrarError("Error... debe ingresar sólo un caracter");
	}while(cadena==null || cadena.length()>1);
	char c=cadena.charAt(0);
	return c;
}
/**
 * Muestra un cuadro de diálogo solicitando el ingreso de un valor real double.
 * @param mensaje especifica la solicitud del valor a ingresar.
 */
public static double leerDouble(String mensaje) {
	String cadena=leerString(mensaje);
	try {
		return Double.parseDouble(cadena);
	} catch (NumberFormatException e) {
		mostrarError("Error... debe ingresar un número ");
		return leerDouble(mensaje);
	}
}
/**
 * Muestra un cuadro de diálogo solicitando el ingreso de un valor real float.
 * @param mensaje especifica la solicitud del valor a ingresar.
 */
public static float leerFloat(String mensaje) {
	String cadena=leerString(mensaje);
	try {
		return Float.parseFloat(cadena);
	} catch (NumberFormatException e) {
		mostrarError("Error... debe ingresar un número ");
		return leerFloat(mensaje);
	}
}
/**
 * Muestra un cuadro de diálogo solicitando el ingreso de un valor entero int.
 * @param mensaje especifica la solicitud del valor a ingresar.
 */
public static int leerInt(String mensaje) {
	String cadena=leerString(mensaje);
	try {
		return Integer.parseInt(cadena);
	} catch (NumberFormatException e) {
		mostrarError("Error... debe ingresar un número ");
		return leerInt(mensaje);
	}
}
/**
 * Muestra un cuadro de diálogo solicitando el ingreso de un valor entero long.
 * @param mensaje especifica la solicitud del valor a ingresar.
 */
public static long leerLong(String mensaje) {
	String cadena=leerString(mensaje);
	try {
		return Long.parseLong(cadena);
	} catch (NumberFormatException e) {
		mostrarError("Error... debe ingresar un número ");
		return leerLong(mensaje);
	}
}
/**
 * Muestra un cuadro de diálogo solicitando el ingreso de una cadena.
 * @param mensaje especifica la solicitud de la cadena a ingresar.
 */
public static String leerString(String mensaje) {
	String cadena;
	do{
		cadena=JOptionPane.showInputDialog(mensaje);
		if(cadena==null)
			mostrarError("Error... no se ha ingresado valor");
	}while(cadena==null);
	return cadena;
}
/**
 * Muestra un cuadro de diálogo con una advertencia especificada en el argumento.
 * @param mensaje es la advertencia a mostrar.
 */
public static void mostrarAdvertencia(String mensaje) {
	JOptionPane.showMessageDialog(null,mensaje,"Advertencia",JOptionPane.WARNING_MESSAGE);
}
/**
 * Muestra un cuadro de diálogo con un mensaje de error especificado en el argumento.
 * @param mensaje es el error a mostrar.
 */
public static void mostrarError(String mensaje) {
	JOptionPane.showMessageDialog(null,mensaje,"Error",JOptionPane.ERROR_MESSAGE);
}
/**
 * Muestra un cuadro de diálogo con una información especificada en el argumento.
 * @param mensaje es la información a mostrar.
 */
public static void mostrarInformacion(String mensaje) {
	JOptionPane.showMessageDialog(null,mensaje,"Información",JOptionPane.INFORMATION_MESSAGE);
}
/**
 * Muestra un cuadro de diálogo con una pregunta especificada en el argumento.
 * @param mensaje es la pregunta a mostrar.
 */
public static void mostrarPregunta(String mensaje) {
	JOptionPane.showMessageDialog(null,mensaje,"Pregunta",JOptionPane.QUESTION_MESSAGE);
}
/**
 * Muestra un cuadro de diálogo con una pregunta especificada en el argumento  y 2 opciones de respuesta a elegir.
 * @param mensaje es la pregunta a mostrar.
 * @return	el entero 0 o 1 si se presionó el botón con la opción YES o NO respectivamente.
 *	Si se cierra este cuadro de diálogo sin presionar algún botón, retorna el entero -1.
 */
public static int mostrarPreguntaOpcion2(String mensaje) {
	return JOptionPane.showOptionDialog(null, mensaje, "Pregunta",
		   				JOptionPane.YES_NO_OPTION, 
		   				JOptionPane.QUESTION_MESSAGE, null, null, null);
}
/**
 * Muestra un cuadro de diálogo con una pregunta especificada en el argumento y 3 opciones de respuesta a elegir.
 * @param mensaje es la pregunta a mostrar.
 * @return	el entero 0, 1 o 2 si se presionó el botón con la opción YES, NO o CANCEL respectivamente. 
 *	Si se cierra este cuadro de diálogo sin presionar algún botón, retorna el entero -1.
 */
public static int mostrarPreguntaOpcion3(String mensaje) {
	return JOptionPane.showOptionDialog(null, mensaje, "Pregunta",
		   				JOptionPane.YES_NO_CANCEL_OPTION, 
		   				JOptionPane.QUESTION_MESSAGE, null, null, null);
}
/**
 * Muestra un cuadro de diálogo con algún tipo de mensaje. No presenta ícono.
 * @param mensaje es el mensaje a mostrar.
 */
public static void mostrarResultado(String mensaje) {
	JOptionPane.showMessageDialog(null,mensaje,"Resultado",JOptionPane.PLAIN_MESSAGE);
}
}


