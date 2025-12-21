import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ComisionesEmpresaComponent } from './comisiones-empresa-component';

describe('ComisionesEmpresaComponent', () => {
  let component: ComisionesEmpresaComponent;
  let fixture: ComponentFixture<ComisionesEmpresaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ComisionesEmpresaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ComisionesEmpresaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
