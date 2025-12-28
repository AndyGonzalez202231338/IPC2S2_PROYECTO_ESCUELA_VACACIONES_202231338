import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CalificacionesComentariosComponent } from './calificaciones-comentarios-component';

describe('CalificacionesComentariosComponent', () => {
  let component: CalificacionesComentariosComponent;
  let fixture: ComponentFixture<CalificacionesComentariosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CalificacionesComentariosComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CalificacionesComentariosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
