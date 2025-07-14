package monostore.backend.models;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class OrderRequest {

  @NotBlank(message = "Shipping address is required")
  private Address shippingAddress;

  @NotBlank(message = "Payment method is required")
  private String paymentMethod;
}
@Data
class Address {
  private String street;
  private String city;
  private String state;
  private String zipCode;
  private String country;
}





