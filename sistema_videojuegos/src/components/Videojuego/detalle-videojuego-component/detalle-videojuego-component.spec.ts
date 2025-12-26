import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetalleVideojuegoComponent } from './detalle-videojuego-component';

describe('DetalleVideojuegoComponent', () => {
  let component: DetalleVideojuegoComponent;
  let fixture: ComponentFixture<DetalleVideojuegoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetalleVideojuegoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DetalleVideojuegoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
