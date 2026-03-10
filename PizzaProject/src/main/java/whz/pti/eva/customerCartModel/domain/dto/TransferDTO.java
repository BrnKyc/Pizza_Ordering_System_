package whz.pti.eva.customerCartModel.domain.dto;


import java.io.Serializable;
import java.math.BigDecimal;

public record TransferDTO(String to, BigDecimal amount) implements Serializable {}
