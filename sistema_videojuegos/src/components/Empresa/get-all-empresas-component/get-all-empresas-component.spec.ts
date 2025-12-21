import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GetAllEmpresasComponent } from './get-all-empresas-component';

describe('GetAllEmpresasComponent', () => {
  let component: GetAllEmpresasComponent;
  let fixture: ComponentFixture<GetAllEmpresasComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GetAllEmpresasComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GetAllEmpresasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
