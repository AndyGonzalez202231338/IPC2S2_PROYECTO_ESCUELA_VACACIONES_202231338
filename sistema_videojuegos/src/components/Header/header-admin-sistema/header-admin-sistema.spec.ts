import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HeaderAdminSistema } from './header-admin-sistema';

describe('HeaderAdminSistema', () => {
  let component: HeaderAdminSistema;
  let fixture: ComponentFixture<HeaderAdminSistema>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HeaderAdminSistema]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HeaderAdminSistema);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
