import { MarketplaceDataService } from './marketplace-data.service';

describe('MarketplaceDataService - Cotizaciones SCRUM-78', () => {
  let marketplace: MarketplaceDataService;

  beforeEach(() => {
    marketplace = new MarketplaceDataService();
  });

  it('CA-78.1: calcula correctamente el subtotal según las noches y el precio por noche', () => {
    const alojamiento = marketplace.accommodations[0];

    const resultado = marketplace.calculateQuote(
      alojamiento,
      '2027-02-01',
      '2027-02-04',
    );

    expect(resultado).not.toBeNull();
    expect(resultado!.nights).toBe(3);
    expect(resultado!.nightlySubtotal).toBe(540000);
    expect(resultado!.nightlySubtotal).toBe(
      alojamiento.pricePerNight * resultado!.nights,
    );
  });

  it('CA-78.2: incluye limpieza y tarifa de servicio en el total', () => {
    const alojamiento = marketplace.accommodations[0];

    const resultado = marketplace.calculateQuote(
      alojamiento,
      '2027-02-01',
      '2027-02-04',
    );

    expect(resultado).not.toBeNull();
    expect(resultado!.cleaningFee).toBe(alojamiento.cleaningFee);
    expect(resultado!.serviceFee).toBe(58000);

    expect(resultado!.total).toBe(
      resultado!.nightlySubtotal
      + resultado!.cleaningFee
      + resultado!.serviceFee
      + resultado!.extras
      - resultado!.longStayDiscount,
    );
  });

  it('CA-78.3: no aplica descuentos que no estén configurados', () => {
    const alojamiento = marketplace.accommodations[0];

    const resultado = marketplace.calculateQuote(
      alojamiento,
      '2027-02-01',
      '2027-02-08',
    );

    expect(resultado).not.toBeNull();
    expect(resultado!.nights).toBe(7);
    expect(resultado!.longStayDiscount).toBe(0);
  });

  it('CA-78.4: no aplica descuentos para estancias inferiores a siete noches', () => {
    const alojamiento = marketplace.accommodations[0];

    const resultado = marketplace.calculateQuote(
      alojamiento,
      '2027-02-01',
      '2027-02-07',
    );

    expect(resultado).not.toBeNull();
    expect(resultado!.nights).toBe(6);
    expect(resultado!.longStayDiscount).toBe(0);
  });

  it('CA-78.5: no agrega cobros por servicios adicionales no configurados', () => {
    const alojamiento = marketplace.accommodations[0];

    const resultado = marketplace.calculateQuote(
      alojamiento,
      '2027-02-01',
      '2027-02-04',
      true,
    );

    expect(resultado).not.toBeNull();
    expect(resultado!.extras).toBe(0);
    expect(resultado!.total).toBe(638000);
  });

  it('CA-78.6: calcula el total sin cobros adicionales no seleccionados', () => {
    const alojamiento = marketplace.accommodations[0];

    const resultado = marketplace.calculateQuote(
      alojamiento,
      '2027-02-01',
      '2027-02-04',
      false,
    );

    expect(resultado).not.toBeNull();
    expect(resultado!.extras).toBe(0);
    expect(resultado!.total).toBe(638000);
  });

  it('CA-78.7: rechaza fechas iguales, invertidas o inválidas', () => {
    const alojamiento = marketplace.accommodations[0];

    expect(
      marketplace.calculateQuote(
        alojamiento,
        '2027-02-04',
        '2027-02-04',
      ),
    ).toBeNull();

    expect(
      marketplace.calculateQuote(
        alojamiento,
        '2027-02-05',
        '2027-02-04',
      ),
    ).toBeNull();

    expect(
      marketplace.calculateQuote(
        alojamiento,
        'fecha-invalida',
        '2027-02-04',
      ),
    ).toBeNull();
  });
});