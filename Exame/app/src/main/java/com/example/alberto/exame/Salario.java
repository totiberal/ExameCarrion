package com.example.alberto.exame;

/**
 * Created by alberto on 11/12/15.
 */
public class Salario {
    private double salario;
    private String mes;

    public Salario(){}

    public Salario(String mes, double salario){
        this.mes=mes;
        this.salario=salario;
    }

    public double getSalario(){
        return salario;
    }

    public String getMes(){
        return mes;
    }

    public void setMes( String mes){
        this.mes=mes;
    }

    public void setSalario(double salario){
        this.salario=salario;
    }

    @Override
    public String toString(){
        return "Mes: "+this.mes+" Salario total="+this.salario;
    }
}
