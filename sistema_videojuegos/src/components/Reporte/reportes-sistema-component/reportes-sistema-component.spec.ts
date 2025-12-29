import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReportesSistemaComponent } from './reportes-sistema-component';

describe('ReportesSistemaComponent', () => {
  let component: ReportesSistemaComponent;
  let fixture: ComponentFixture<ReportesSistemaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReportesSistemaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReportesSistemaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
