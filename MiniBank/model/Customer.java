
package model;

import java.io.Serializable;
import java.util.Objects;

public class Customer implements Cloneable, Serializable {

    private static final long serialVersionUID = 1L;

    private static int customerCounter = 100;

    private final String customerId;
    private String name;
    private String email;
    private String mobile;
    private Address address;

    public Customer(
            String name,
            String email,
            String mobile,
            Address address) {

        this.customerId = "CUST" + (++customerCounter);
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.address = address;
    }

    public Customer(
            String name,
            String email,
            String mobile) {

        this(name, email, mobile, null);
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public Customer clone() {
        try {
            Customer copy = (Customer) super.clone();

            if (address != null) {
                copy.address = address.clone();
            }

            return copy;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId='" + customerId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", mobile='" + mobile + '\'' +
                ", address=" + address +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Customer)) {
            return false;
        }

        Customer other = (Customer) obj;
        return customerId.equals(other.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId);
    }

    public static class Address
            implements Serializable, Cloneable {

        private static final long serialVersionUID = 1L;

        private String line;
        private String city;
        private String pincode;

        public Address(
                String line,
                String city,
                String pincode) {

            this.line = line;
            this.city = city;
            this.pincode = pincode;
        }

        public String getLine() {
            return line;
        }

        public String getCity() {
            return city;
        }

        public String getPincode() {
            return pincode;
        }

        @Override
        public Address clone() {
            try {
                return (Address) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }

        @Override
        public String toString() {
            return line + ", " + city + " - " + pincode;
        }
    }
}
