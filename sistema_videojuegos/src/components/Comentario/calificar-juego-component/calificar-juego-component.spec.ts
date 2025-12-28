import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CalificarJuegoComponent } from './calificar-juego-component';

describe('CalificarJuegoComponent', () => {
  let component: CalificarJuegoComponent;
  let fixture: ComponentFixture<CalificarJuegoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CalificarJuegoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CalificarJuegoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
