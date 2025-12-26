import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CarteraUsuarioComponent } from './cartera-usuario-component';

describe('CarteraUsuarioComponent', () => {
  let component: CarteraUsuarioComponent;
  let fixture: ComponentFixture<CarteraUsuarioComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CarteraUsuarioComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CarteraUsuarioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
