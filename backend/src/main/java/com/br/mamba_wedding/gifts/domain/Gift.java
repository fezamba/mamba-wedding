package com.br.mamba_wedding.gifts.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "gifts", indexes = {
		@Index(name = "idx_gifts_status", columnList = "status")
})

public class Gift {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

    @Version
    private Long version;

	@Column(nullable = false, length = 120)
	private String nome;

	@Column(length = 500)
	private String descricao;

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal valor;

	@Column(nullable = false)
    private Integer cotasTotais;

	@Column(length = 255)
	private String imagemUrl;

    // Seria um link para pagar no mercado pago, paypal, etc.
	@Column(length = 255)
	private String linkCompra;

	@OneToMany(mappedBy = "gift", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GiftTransaction> transacoes = new ArrayList<>();

	public boolean isEsgotado() {
			return getCotasDisponiveis() <= 0;
		}

	public Integer getCotasDisponiveis() {
			int cotasOcupadas = transacoes.stream()
				.filter(t -> t.getStatus() != TransactionStatus.CANCELADO)
				.mapToInt(GiftTransaction::getQuantidadeCotas)
				.sum();
			return cotasTotais - cotasOcupadas;
		}
}