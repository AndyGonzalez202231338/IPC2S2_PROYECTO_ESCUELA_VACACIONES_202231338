import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HeaderAdminEmpresa } from './header-admin-empresa';

describe('HeaderAdminEmpresa', () => {
  let component: HeaderAdminEmpresa;
  let fixture: ComponentFixture<HeaderAdminEmpresa>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HeaderAdminEmpresa]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HeaderAdminEmpresa);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
