import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ComprarVideojuegoComponent } from './comprar-videojuego-component';

describe('ComprarVideojuegoComponent', () => {
  let component: ComprarVideojuegoComponent;
  let fixture: ComponentFixture<ComprarVideojuegoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ComprarVideojuegoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ComprarVideojuegoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
