import {ComponentFixture, TestBed} from '@angular/core/testing';

import {PlatformComponentsComponent} from './platform-components.component';

describe('PlatformComponentsComponent', () => {
  let component: PlatformComponentsComponent;
  let fixture: ComponentFixture<PlatformComponentsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PlatformComponentsComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(PlatformComponentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
