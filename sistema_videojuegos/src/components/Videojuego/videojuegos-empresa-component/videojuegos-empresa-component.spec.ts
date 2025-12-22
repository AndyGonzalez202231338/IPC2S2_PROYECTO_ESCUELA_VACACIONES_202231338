import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VideojuegosEmpresaComponent } from './videojuegos-empresa-component';

describe('VideojuegosEmpresaComponent', () => {
  let component: VideojuegosEmpresaComponent;
  let fixture: ComponentFixture<VideojuegosEmpresaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VideojuegosEmpresaComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VideojuegosEmpresaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
