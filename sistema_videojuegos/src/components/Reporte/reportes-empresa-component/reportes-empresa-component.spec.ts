import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReportesEmpresaComponent } from './reportes-empresa-component';

describe('ReportesEmpresaComponent', () => {
  let component: ReportesEmpresaComponent;
  let fixture: ComponentFixture<ReportesEmpresaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReportesEmpresaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReportesEmpresaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
